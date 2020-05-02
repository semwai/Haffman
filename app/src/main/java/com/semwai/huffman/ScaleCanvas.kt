package com.semwai.huffman

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View


@SuppressLint("ViewConstructor")
open class ScaleCanvas(context: Context) : View(context) {

    private var xOffset = 0.0F
    private var yOffset = 0.0F
    private var scale = 1F
    lateinit var canvas: Canvas

    override fun onDraw(cnv: Canvas) {
        canvas = cnv
        canvas.translate(-xOffset, -yOffset)
        //Log.v("xOffset", xOffset.toString())
        canvas.scale(scale, scale)

        //val ar = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        //canvas.matrix.getValues(ar)
        //Log.v("ar", "1\n\n" + ar.map { "$it " }.chunked(3).joinToString("\n"))

        //matrix.postScale(2f, 2f)
        //matrix.reset()
    }

    private var startX = 0.0f
    private var startY = 0.0f
    private var touchCounter = 0
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleDetector.onTouchEvent(event)


        if (!isScaleProcess)
            when (event?.actionMasked) {
                MotionEvent.ACTION_MOVE -> {
                    if (event.getPointerId(event.actionIndex) == 0)
                            xOffset += (startX - event.x)
                            yOffset += (startY - event.y)
                            startX = event.x
                            startY = event.y
                    //event.pointerCount
                    //event.getPointerId(event.poi)
                }
                //actionpointerdown\up - не первый палец поднимается, отпускается. есть id
                MotionEvent.ACTION_DOWN ->{
                    touchCounter++
                    startX = event.x
                    startY = event.y


                }
                MotionEvent.ACTION_UP -> {
                    touchCounter--
                }
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
            if (detector.scaleFactor > 1 && scale * detector.scaleFactor < maxScale || detector.scaleFactor < 1 && scale * detector.scaleFactor > minScale) {
                scale *= detector.scaleFactor
                //xOffset *= detector.scaleFactor
                //yOffset *= detector.scaleFactor
                xOffset = (xOffset + detector.focusX) / scale
                //yOffset = (yOffset + detector.focusY) / scale
            }

            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            isScaleProcess = true
            return super.onScaleBegin(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            super.onScaleEnd(detector)
            isScaleProcess = false
        }
    }
    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
    private var isScaleProcess =
        false // при скалировании будем блокировать другое перемещение сцены
}

