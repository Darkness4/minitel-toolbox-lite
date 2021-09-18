package com.minitel.toolboxlite.presentation.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import com.minitel.toolboxlite.R
import com.minitel.toolboxlite.presentation.utils.drawTriangle

class BackgroundView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {
    companion object {
        val DEFAULT_COLOR = Color.argb(100, 0, 255, 0)
    }

    private var w = 0
    private var h = 0
    private val foregroundPaint = Paint()
    private val polygonPath1 = Path()
    private val polygonPath2 = Path()
    private val polygonPath3 = Path()
    private val polygonPath4 = Path()
    private val polygonPath5 = Path()
    private val polygonPath6 = Path()

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.BackgroundView, 0, 0).apply {
            try {
                val colorAttr = ColorUtils.setAlphaComponent(
                    getColor(
                        R.styleable.BackgroundView_color,
                        DEFAULT_COLOR
                    ), 100
                )
                foregroundPaint.apply {
                    style = Paint.Style.FILL
                    color = colorAttr
                }
            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        this.w = w
        this.h = h
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {
            drawPath(
                polygonPath1.drawTriangle(
                    0f to 0f,
                    w / 3f to 0f,
                    0f to h * 4f / 5f,
                ),
                foregroundPaint
            )

            drawPath(
                polygonPath2.drawTriangle(
                    w * 2f / 5f to 0f,
                    w.toFloat() to 0f,
                    w.toFloat() to h / 2f,
                ),
                foregroundPaint
            )

            drawPath(
                polygonPath3.drawTriangle(
                    0f to h.toFloat(),
                    w.toFloat() to h * 4f / 5f,
                    w.toFloat() to h.toFloat()
                ),
                foregroundPaint
            )

            drawPath(
                polygonPath4.drawTriangle(
                    0f to h / 2f,
                    w / 2f to h.toFloat(),
                    0f to h.toFloat(),
                ),
                foregroundPaint
            )

            drawPath(
                polygonPath5.drawTriangle(
                    w * 3f / 4f to h.toFloat(),
                    w.toFloat() to h.toFloat(),
                    w.toFloat() to h / 4f,
                ),
                foregroundPaint
            )

            drawPath(
                polygonPath6.drawTriangle(
                    0f to h / 3f,
                    0f to 0f,
                    w.toFloat() to 0f,
                ),
                foregroundPaint
            )
        }
    }
}