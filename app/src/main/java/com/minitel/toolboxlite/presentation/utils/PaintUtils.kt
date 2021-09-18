package com.minitel.toolboxlite.presentation.utils

import android.graphics.Path

fun Path.drawTriangle(
    a: Pair<Float, Float>,
    b: Pair<Float, Float>,
    c: Pair<Float, Float>
) = apply {
    reset()
    moveTo(a.first, a.second)
    lineTo(b.first, b.second)
    lineTo(c.first, c.second)
    lineTo(a.first, a.second)
}