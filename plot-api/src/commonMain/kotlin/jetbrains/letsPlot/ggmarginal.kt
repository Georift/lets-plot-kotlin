/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.letsPlot

import jetbrains.datalore.plot.config.Option
import jetbrains.letsPlot.MarginalLayerUtil.toMarginal
import jetbrains.letsPlot.MarginalLayerUtil.toSize
import jetbrains.letsPlot.intern.*

/**
 * Converts a given geometry layer to a marginal layer.
 * You can add one or more marginal layers to a plot to create a marginal plot.
 *
 * @param sides string.
 *     A string specifying which sides of the plot the marginal layer will appear on.
 *     It should be set to a string containing any of "trbl", for top, right, bottom, and left.
 * @param size number or list of numbers.
 *     Size of marginal geometry (width or height, depending on the margin side) as a fraction of the entire
 *     plotting area of the plot.
 *     The value should be in range [0.01..0.95].
 * @param layer marginal geometry layer.
 *     The result of calling of the `geomXxx()` / `statXxx()` function.
 *     Marginal plot works best with `density`,`histogram`,`boxplot`,`violin` ans `freqpoly` geometry layers.
 */

@Suppress("SpellCheckingInspection")
fun ggmarginal(
    sides: String,
    size: Any? = null,
    layer: Layer
): FeatureList {
    require(sides.length <= 4) { "'sides' must be a string containing 1 to 4 chars: 'l','r','t','b'." }

    val list = ArrayList<Feature>()
    sides.indices.forEach { i ->
        list += toMarginal(sides[i], toSize(size, i), layer)
    }
    return FeatureList(list)
}


private object MarginalLayerUtil {
    fun toSize(size: Any?, index: Int): Number? {
        return when (size) {
            null -> null
            is Number -> size
            is List<*> -> {
                require(size.all { it is Number }) { "'size' must contain only numbers: $size." }
                size[index] as Number
            }
            else -> error("Invalid 'size' type: $size. Expected: number or list of numbers.")
        }
    }

    fun toMarginal(side: Char, size: Number?, layer: Layer): Feature {
        require(side in listOf('l', 'r', 't', 'b')) { "Invalid 'side' value: $side. Valid values: 'l','r','t','b'." }
        if (size != null) {
            require(size.toDouble() in 0.01..0.95) { "Invalid 'size' value: $size. Should be in range [0.01..0.95]." }
        }

        return MarginalLayer(layer, side, size)
    }

    private class MarginalLayer(
        layer: Layer,
        marginalSide: Char,
        marginalSize: Number?
    ) : Layer(
        mapping = layer.mapping + getAdditionalMapping(getLayerKind(layer), marginalSide),
        data = layer.data,
        geom = layer.geom,
        stat = layer.stat,
        position = layer.position,
        showLegend = layer.showLegend,
        sampling = layer.sampling,
        tooltips = null,
        orientation = null
    ) {
        override val parameters = layer.parameters +
                getMarginalOptions(marginalSide, marginalSize) +
                getOrientationOptions(getLayerKind(layer), marginalSide)
    }

    private fun getLayerKind(layer: Layer) = when (layer.stat.kind) {
        StatKind.BIN -> GeomKind.HISTOGRAM
        StatKind.YDENSITY -> GeomKind.VIOLIN
        StatKind.DENSITY -> GeomKind.DENSITY
        StatKind.BOXPLOT -> GeomKind.BOX_PLOT
        else -> layer.geom.kind
    }

    private fun getMarginalOptions(side: Char, size: Number?) = Options(
        mapOf(
            Option.Layer.MARGINAL to true,
            Option.Layer.Marginal.SIDE to side,
            Option.Layer.Marginal.SIZE to size
        )
    )

    private fun getAdditionalMapping(layerKind: GeomKind, side: Char): Options {
        // For 'histogram' set mapping of x or y to '..density..' for compatibility with 'density' geom.
        val addedMapping = HashMap<String, Any>()
        if (layerKind == GeomKind.HISTOGRAM) {
            if (side in listOf('l', 'r')) {
                addedMapping["x"] = "..density.."
            } else if (side in listOf('t', 'b')) {
                addedMapping["y"] = "..density.."
            }
        }
        return Options(addedMapping)
    }

    private fun getOrientationOptions(layerKind: GeomKind, side: Char): Options {
        // Choose a proper orientation
        val autoSettings = HashMap<String, Any>()
        if (side in listOf('l', 'r') && layerKind in listOf(GeomKind.HISTOGRAM, GeomKind.DENSITY, GeomKind.FREQPOLY)) {
            autoSettings[Option.Layer.ORIENTATION] = "y"
        }
        if (layerKind in listOf(GeomKind.BOX_PLOT, GeomKind.VIOLIN)) {
            if (side in listOf('l', 'r')) {
                autoSettings["x"] = 0
            } else if (side in listOf('t', 'b')) {
                autoSettings["y"] = 0
                autoSettings[Option.Layer.ORIENTATION] = "y"
            }
        }
        return Options(autoSettings)
    }
}