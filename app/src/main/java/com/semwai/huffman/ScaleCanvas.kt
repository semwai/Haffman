package com.semwai.huffman

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.concurrent.thread



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
    private var touchCounter = 0
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mScaleDetector.onTouchEvent(event)

        val fingerIndex = event?.actionIndex ?: 0
        val fingerId = event?.getPointerId(fingerIndex) ?: 0

        if (!isScaleProcess)
            when (event?.actionMasked) {
                MotionEvent.ACTION_MOVE -> {
                    val egx = event.getX(fingerIndex)
                    val egy = event.getY(fingerIndex)
                    xOffset += (startX[fingerId]?:egx) - egx
                    yOffset += (startY[fingerId]?:egy) - egy
                    startX[fingerId] = egx
                    startY[fingerId] = egy
                }
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    touchCounter++
                    startX[fingerId] = event.getX(fingerIndex)
                    startY[fingerId] = event.getY(fingerIndex)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
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
            val newScale = scale * detector.scaleFactor
            if (newScale in minScale..maxScale) {
                scale = newScale
                //мне кажется я решил проблему не совсем как вы предложили, но это вполне работает
                xOffset =
                    xOffset * detector.scaleFactor + detector.focusX * (detector.scaleFactor - 1)
                yOffset =
                    yOffset * detector.scaleFactor + detector.focusY * (detector.scaleFactor - 1)
            }
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            isScaleProcess = true
            return super.onScaleBegin(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            super.onScaleEnd(detector)
            //позволяет убрать лишнее поддергивание после окончания скейлинга.
            thread {
                Thread.sleep(50)
                isScaleProcess = false
            }
        }
    }
    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
    private var isScaleProcess =
        false // при скалировании будем блокировать другое перемещение сцены
}

