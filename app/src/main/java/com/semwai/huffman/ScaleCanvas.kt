package com.semwai.huffman

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

open class ScaleCanvas(context: Context) : View(context) {

    private var xOffset = 0.0F
    private var yOffset = 0.0F
    private var scale = 1F
    lateinit var canvas: Canvas

    override fun onDraw(cnv: Canvas) {
        canvas = cnv
        canvas.translate(-xOffset, -yOffset)
        canvas.scale(scale, scale)
    }

    private var startX = mutableMapOf<Int, Float>()
    private var startY = mutableMapOf<Int, Float>()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(event)
        val fingerId = event.getPointerId(0)
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val egx = event.x
                val egy = event.y
                xOffset += (startX[fingerId] ?: egx) - egx
                yOffset += (startY[fingerId] ?: egy) - egy
                startX[fingerId] = egx
                startY[fingerId] = egy
            }
        }
        for (i in 0 until event.pointerCount) {
            startX[i] = event.getX(i)
            startY[i] = event.getY(i)
        }
        //перерисовка после сдвигов
        invalidate()
        //если иначе, то не работает move
        return true//super.onTouchEvent(event)
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        val minScale = 0.1f
        val maxScale = 4.0f
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val newScale = scale * detector.scaleFactor
            if (newScale in minScale..maxScale) {
                scale = newScale
                xOffset =
                    xOffset * detector.scaleFactor + detector.focusX * (detector.scaleFactor - 1)
                yOffset =
                    yOffset * detector.scaleFactor + detector.focusY * (detector.scaleFactor - 1)
            }
            return true
        }
    }
    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
}

