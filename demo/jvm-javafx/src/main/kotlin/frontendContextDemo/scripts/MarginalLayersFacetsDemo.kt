/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package frontendContextDemo.scripts

import demoData.AutoMpg
import frontendContextDemo.ScriptInJfxContext
import jetbrains.letsPlot.coordFixed
import jetbrains.letsPlot.facet.facetGrid
import jetbrains.letsPlot.facet.facetWrap
import jetbrains.letsPlot.geom.geomDensity
import jetbrains.letsPlot.geom.geomHistogram
import jetbrains.letsPlot.geom.geomPoint
import jetbrains.letsPlot.ggmarginal
import jetbrains.letsPlot.ggplot
import jetbrains.letsPlot.label.ggtitle
import jetbrains.letsPlot.themeGrey

object MarginalLayersFacetsDemo {

    @JvmStatic
    fun main(args: Array<String>) {
        ScriptInJfxContext.eval("Marginal layers (facets).") {

            val mpgData = AutoMpg.map()
            val sides = "ltrb"
            val sizes = listOf(0.1, 0.1, 0.2, 0.2)

            val p = ggplot(mpgData) {
                x = "engine horsepower"
                y = "miles per gallon"
                color = "origin of car"
            } + geomPoint() +
                    ggmarginal(
                        sides,
                        sizes,
                        layer = geomHistogram(bins = 10, color = "white") { fill = "origin of car" }) +
                    ggmarginal(sides, sizes, layer = geomDensity(color = "red", fill = "blue", alpha = 0.1)) +
                    themeGrey()

            (p + facetGrid(y = "origin of car", yOrder = 0) + ggtitle("Grid")).show()
            (p + facetGrid(y = "origin of car", yOrder = 0) + coordFixed() + ggtitle("Grid, coord=fixed")).show()
            (p + facetGrid(y = "origin of car", yOrder = 0, scales = "free_y") + ggtitle("Grid, scales=free_y")).show()

            (p + facetWrap(facets = "number of cylinders", format = "{d} cyl") + ggtitle("Wrap")).show()
            (p + facetWrap(
                facets = "number of cylinders",
                format = "{d} cyl"
            ) + coordFixed(ylim = 0 to 100) + ggtitle("Wrap, coord=fixed")).show()
            (p + facetWrap(
                facets = "number of cylinders",
                format = "{d} cyl",
                scales = "free_x"
            ) + ggtitle("Wrap, scales=free_x")).show()
        }
    }
}