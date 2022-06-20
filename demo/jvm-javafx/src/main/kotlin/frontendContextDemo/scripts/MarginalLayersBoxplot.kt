/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package frontendContextDemo.scripts

import frontendContextDemo.ScriptInJfxContext

object MarginalLayersBoxplot {
    @JvmStatic
    fun main(args: Array<String>) {
        ScriptInJfxContext.eval("Marginal layers.") {
            MarginalLayers.marginalLayersDemo(boxplot = true)
        }
    }
}