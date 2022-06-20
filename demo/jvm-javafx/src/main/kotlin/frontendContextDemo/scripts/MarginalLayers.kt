/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package frontendContextDemo.scripts

import frontendContextDemo.ScriptInJfxContext
import jetbrains.letsPlot.geom.geomBoxplot
import jetbrains.letsPlot.geom.geomDensity
import jetbrains.letsPlot.geom.geomHistogram
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.ggmarginal
import jetbrains.letsPlot.ggplot
import jetbrains.letsPlot.intern.Plot
import jetbrains.letsPlot.label.ggtitle

object MarginalLayers {
    private val X = listOf(
        -0.85682293, -2.3911234, -2.42744314, -1.94456221, -3.08116168,
        -2.82149096, -2.75909911, -0.5976029, -2.28422114, -2.87574481,
        -2.44764864, 0.62140045, -2.78634844, -0.77798494, -1.84775972,
        -0.68368036, -1.16449909, -2.54485003, -2.2036209, -1.68242049
    )
    private val Y = listOf(
        -1.52369374, 0.10800142, -0.04456882, 0.24980294, 0.07656286,
        1.18430493, 0.60070862, -1.3039589, 0.54056665, 0.4756451,
        1.23440038, -2.64721071, 0.86629033, -1.01436946, -0.30687369,
        -1.23137015, -0.41737117, 0.54053481, -0.71151953, -1.37503288
    )

    private val DATA = mapOf(
        "x" to X,
        "y" to Y
    )

    private fun marginalPlot(
        marginSides: String,
        marginSizes: List<Double>,
        boxplot: Boolean
    ): Plot {
        return ggplot(DATA) { x = "x"; y = "y" } +
                ggtitle("Margins: $marginSides") +
                geomPoint(color = "black", alpha = 0.6, size = 5) +
                if (boxplot) {
                    ggmarginal(marginSides, marginSizes, geomBoxplot())
                } else {
                    ggmarginal(marginSides, marginSizes, layer = geomHistogram(bins = 10, color = "white")) +
                            ggmarginal(
                                marginSides, marginSizes,
                                layer = geomDensity(fill = "blue", color = "red", alpha = 0.1)
                            )
                }
    }

    internal fun marginalLayersDemo(boxplot: Boolean = false) {
        (marginalPlot("l", listOf(0.1), boxplot)).show()

        (marginalPlot("r", listOf(0.2), boxplot)).show()
        (marginalPlot("lr", listOf(0.1, 0.2), boxplot)).show()

        (marginalPlot("t", listOf(0.1), boxplot)).show()
        (marginalPlot("b", listOf(0.2), boxplot)).show()
        (marginalPlot("tb", listOf(0.1, 0.2), boxplot)).show()

        (marginalPlot("tr", listOf(0.1, 0.2), boxplot)).show()
        (marginalPlot("bl", listOf(0.2, 0.1), boxplot)).show()
        (marginalPlot("trbl", listOf(0.1, 0.2, 0.2, 0.1), boxplot)).show()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        ScriptInJfxContext.eval("Marginal layers.") {
            marginalLayersDemo()
        }
    }
}