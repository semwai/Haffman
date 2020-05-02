package com.semwai.huffman


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_draw.*
import kotlinx.android.synthetic.main.switch_layout.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.system.exitProcess


class Drawer : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setCustomView(R.layout.switch_layout)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        switchForActionBar.setOnClickListener {
            lv1.visibility = if (lv1.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

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
private class HaffmanDrawerView(context: Context, val root: Huffman) : ScaleCanvas(context) {
    //2 переменные хранят в себе цвета и другие свойства для отрисовки графисеских примитивов
    var fill = Paint(Paint.ANTI_ALIAS_FLAG)
    var stroke = Paint(Paint.ANTI_ALIAS_FLAG)
    var nodeRadius = 0F

    //Расстояния между нодами
    private val widthMap = offsetMaster(root.rootNode)

    init {
        fill.color = Color.GREEN
        stroke.strokeWidth = 3F
        stroke.color = Color.BLACK
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(cnv: Canvas) {
        super.onDraw(cnv)
        canvas.drawARGB(200, 102, 204, 255)
        canvas.drawRect(-1005f, -505f, 2005f, 2505f, Paint().apply { color = Color.rgb(0, 0, 0) })
        canvas.drawRect(
            -1000f,
            -500f,
            2000f,
            2500f,
            Paint().apply { color = Color.rgb(102, 204, 255) })
        nodeRadius = 25f
        stroke.textSize = 10f
        //начинаем отрисовывать граф.
        drawNode("start", null, width / 2f, 0f)
        drawGraph(root.rootNode, width / 2f, 0f)

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
}

