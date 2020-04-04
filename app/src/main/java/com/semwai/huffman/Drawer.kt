package com.semwai.huffman

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_draw.*
import kotlin.math.*


class Drawer : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = intent

        val mainList =
            Huffman(i.getStringExtra("map")).getPath().toList().sortedWith(compareBy { it.second })

        setContentView(R.layout.activity_draw)

        //боковой текст с готовыми значениями
        for (e in mainList) {
            val tv = TextView(this).apply {
                text = "${e.first}\t-\t${e.second}"
            }
            lv1.addView(tv)
        }

        val hdv = HaffmanDrawerView(this, mainList)
        lay.addView(hdv)

        buttonMinus.setOnClickListener {
            hdv.addScale(-0.1F)
        }
        buttonPlus.setOnClickListener {
            hdv.addScale(0.1F)
        }
    }
}


@SuppressLint("ViewConstructor")
private class HaffmanDrawerView(context: Context, val mainList: List<Pair<Char, String>>) :
    View(context) {
    //2 переменные хранят в себе цвета и другие свойства для отрисовки графисеских примитивов
    var fill = Paint(Paint.ANTI_ALIAS_FLAG)
    var stroke = Paint(Paint.ANTI_ALIAS_FLAG)
    var nodeRadius = 0F
    lateinit var canvas: Canvas
    private var xOffset = 0.0F
    private var yOffset = 0.0F
    private var scale = 1.0F
    private val mScaleGestureDetector =
        ScaleGestureDetector.SimpleOnScaleGestureListener()

    init {
        fill.color = Color.GREEN
        stroke.strokeWidth = 3F
        stroke.color = Color.BLACK
    }

    override fun onDraw(cnv: Canvas) {
        //поворот и координатное преобразование. Так более оптимально представляется граф.
        canvas = cnv
        canvas.rotate(90F)
        canvas.translate(-width * (scale - 1) - xOffset, -width * 1F - yOffset)
        canvas.scale(scale, scale)
        //canvas.drawARGB(80, 102, 204, 255)
        //попытка сделать отрисовку адаптивной.
        nodeRadius = (width + height) / 46F
        stroke.textSize = (width + height) / 88F
        //начинаем отрисовывать граф.
        drawNode("start", null, (height / 2F), -2 * nodeRadius)
        drawGraph(mainList, (height / 2F), 0F)
    }

    /***
     *
     */
    private fun drawGraph(list: List<Pair<Char, String>>, x0: Float, y0: Float, k: Float = 8F) {
        if (list.isEmpty()) {
            return
        }
        val xOffset = nodeRadius * 1.6F
        val yOffset = nodeRadius * 2.2F
        val y = y0 + yOffset
        //Данный коэффициент влияет на расстояние между соседними элементами.
        //Чем дальше граф идет вниз, тем меньше потомков у детей.
        //Им не нужно большое расстояние между ними.
        val nextK = if (k <= 2) 1F else k / 2F
        //Разделяю на 2 группы, одна пойдет налево, вторая - направо.
        val list1 = list.filter { it.second.firstOrNull() == '1' }
        val list0 = list.filter { it.second.firstOrNull() == '0' }
        if (list1.isNotEmpty()) {
            drawNode(list1.map { it.first }.joinToString(""), true, x0 - k * xOffset, y0)
            drawGraph(list1.map { it.first to it.second.drop(1) }, x0 - k * xOffset, y, nextK)
            drawEdge(x0, y0 - yOffset, x0 - k * xOffset, y0)
        }
        if (list0.isNotEmpty()) {
            drawNode(list0.map { it.first }.joinToString(""), false, x0 + k * xOffset, y0)
            drawGraph(list0.map { it.first to it.second.drop(1) }, x0 + k * xOffset, y, nextK)
            drawEdge(x0, y0 - yOffset, x0 + k * xOffset, y0)
        }
    }

    /***
     *
     * @text - буква или набор букв, если это не конечная точка.
     * @x - смещение по X
     * @y - смещение по Y
     * @dir? - флаг, определяющий какой бит принаджежит данной букве, 0 или 1. Если значение = null, то это не конечный элемент.
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

    fun addScale(delta: Float) {
        if (scale > -delta && scale < 2 - delta) {
            scale += delta
            this.invalidate()
        }
    }

    private var startX = 0.0f
    private var startY = 0.0f
    private var dX = 0.0f
    private var dY = 0.0f
    private var tapCount = 0
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_MOVE -> {
                if (tapCount == 1) {
                    //Х поменяно на У, т.к данный график перевернут на 90 градусов
                    val qX = (startY - event.y)
                    val qY = (startX - event.x)
                    if (abs(dX.toInt() - qX) > 1.0 && abs(dY - qY) > 1.0) {
                        dX = qX
                        dY = qY
                        xOffset += (dX) / (10 * scale)
                        yOffset += -(dY) / (10 * scale)
                    }
                }
            }
            MotionEvent.ACTION_DOWN -> {
                if (tapCount == 0) {
                    tapCount++
                    startX = event.x
                    startY = event.y
                }
            }
            MotionEvent.ACTION_UP -> {
                if (tapCount == 1)
                    tapCount = 0
            }
        }
        //перерисовка после сдвиги
        invalidate()
        //если иначе, то не работает move
        return true//super.onTouchEvent(event)
    }

}