package com.semwai.huffman

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_draw.*
import kotlin.collections.HashMap
import kotlin.math.*
import kotlin.system.exitProcess


class Drawer : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = intent
        val serValue = i.getSerializableExtra("map")
        if (serValue == null) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()
            exitProcess(-1)
        }
        val hfm = Huffman(serValue as HashMap<Char, Int>)
        setContentView(R.layout.activity_draw)
        //боковой текст с готовыми значениями
        for (e in hfm.getPath()) {
            val tv = TextView(this).apply {
                text = "${e.key}\t-\t${e.value}"
                typeface = Typeface.MONOSPACE
            }
            lv1.addView(tv)
        }
        val hdv = HaffmanDrawerView(this, hfm)
        lay.addView(hdv)
    }
}


@SuppressLint("ViewConstructor")
private class HaffmanDrawerView(context: Context, val root: Huffman) :
    View(context) {
    //2 переменные хранят в себе цвета и другие свойства для отрисовки графисеских примитивов
    var fill = Paint(Paint.ANTI_ALIAS_FLAG)
    var stroke = Paint(Paint.ANTI_ALIAS_FLAG)
    var nodeRadius = 0F
    lateinit var canvas: Canvas
    private var xOffset = 0.0F
    private var yOffset = 0.0F
    private var scale = 1F
    //Расстояния между нодами
    private val widthMap = offsetMaster(root.getRootNode())

    init {
        fill.color = Color.GREEN
        stroke.strokeWidth = 3F
        stroke.color = Color.BLACK
    }


    override fun onDraw(cnv: Canvas) {
        canvas = cnv
        canvas.translate(-xOffset, yOffset)
        canvas.scale(scale, scale)
        canvas.drawARGB(255, 102, 204, 255)
        //попытка сделать отрисовку адаптивной.
        nodeRadius = (width + height) / 46F
        stroke.textSize = (width + height) / 88F
        //начинаем отрисовывать граф.
        drawNode("start", null, width / 2f, 0f)
        drawGraph(root.getRootNode(), width / 2f, 0f)
    }

    private fun drawGraph(element: Huffman.Point, x0: Float, y0: Float) {
        val rX = nodeRadius * 1.5f
        val rY = nodeRadius * 2.0f
        if (element is Huffman.HChar) {
            drawNode(element.chars, null, x0, y0)
        } else {
            with(element as Huffman.HNode) {
                val (aX, aY) = widthMap[a.chars] ?: error("")
                val (bX, bY) = widthMap[b.chars] ?: error("")
                drawNode(a.chars, true, x0 - aX * rX, y0 + aY * rY)
                drawEdge(x0, y0, x0 - aX * rX, y0 + aY * rY)

                drawNode(b.chars, false, x0 + bX * rX, y0 + bY * rY)
                drawEdge(x0, y0, x0 + bX * rX, y0 + bY * rY)

                drawGraph(a, x0 - aX * rX, y0 + aY * rY)
                drawGraph(b, x0 + bX * rX, y0 + bY * rY)
            }
        }
    }

    /***
     * @text - буква или набор букв, если это не конечная точка.
     * @x - смещение по X
     * @y - смещение по Y
     * @dir? - флаг, показывающий в какую сторону пошла данная ветка. Если null - то это конечный элемент
     */
    private fun drawNode(text: String, dir: Boolean?, x: Float, y: Float) {

        fill.color = Color.BLACK
        canvas.drawCircle(x, y, nodeRadius + 2, fill)
        fill.color = Color.parseColor("#008577")
        canvas.drawCircle(x, y, nodeRadius, fill)
        canvas.drawText(text, x - stroke.textSize * text.length / 4, y + 4, stroke)
        if (dir != null) {
            canvas.drawText(if (dir) "1" else "0", x - nodeRadius - 15, y + 4, stroke)
        }
    }

    /***
     * Просто линия, соединяющая 2 Node для красивого отображения
     */
    private fun drawEdge(x1: Float, y1: Float, x2: Float, y2: Float) {
        val angle = atan2(y2.toDouble() - y1.toDouble(), x2.toDouble() - x1.toDouble())
        canvas.drawLine(
            x1 + nodeRadius * cos(angle).toFloat(),
            y1 + nodeRadius * sin(angle).toFloat(),
            x2 - nodeRadius * cos(angle).toFloat(),
            y2 - nodeRadius * sin(angle).toFloat(),
            stroke
        )
    }

    private var startX = 0.0f
    private var startY = 0.0f
    private var isDown = false
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_MOVE -> {
                if (isDown) {

                    xOffset += (startX - event.x)
                    yOffset += -(startY - event.y)
                    //}
                    startX = event.x
                    startY = event.y
                }
            }
            MotionEvent.ACTION_DOWN -> {
                if (!isDown) {
                    isDown = true
                    startX = event.x
                    startY = event.y
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isDown)
                    isDown = false
            }
        }
        mScaleDetector.onTouchEvent(event)
        //перерисовка после сдвигов
        invalidate()
        //если иначе, то не работает move
        return true//super.onTouchEvent(event)
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        val minScale = 0.1f
        val maxScale = 4.0f
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scale *= detector.scaleFactor
            if (scale in minScale..maxScale) {
                xOffset *= detector.scaleFactor
                yOffset *= detector.scaleFactor
            }
            //Ограничиваем масштабирование
            scale = max(minScale, min(scale, maxScale))
            return true
        }
    }
    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)
}

