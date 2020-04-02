package com.semwai.haffman

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class Drawer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = intent
        setContentView(HaffmanDrawerView(this, i.getStringExtra("map"))).toString()
        /*val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        Log.v("Width", "" + width)
        Log.v("height", "" + height)*/
    }

}


@SuppressLint("ViewConstructor")
internal class HaffmanDrawerView(context: Context, inputString: String) : View(context) {
    //2 переменные хранят в себе цвета и другие свойства для отрисовки графисеских примитивов
    var fill = Paint(Paint.ANTI_ALIAS_FLAG)
    var stroke = Paint(Paint.ANTI_ALIAS_FLAG)
    var nodeRadius = 0F
    lateinit var canvas: Canvas

    //Итоговый результат. Сожержит мары вида буква - путь (строка вида 010101).
    //В дальнейшем при отрисовке, первые элементы будут группироваться по первой букве путя.
    private val mainList = Haffman(inputString).getPath().toList().sortedWith(compareBy { it.second })

    init {
        fill.color = Color.GREEN
        stroke.strokeWidth = 3F
        stroke.color = Color.BLACK
    }

    override fun onDraw(cnv: Canvas) {
        //поворот и координатное преобразование. Так более оптимально представляется граф.
        canvas = cnv
        canvas.rotate(90F)
        canvas.translate(0F, -width * 1.0F)
        canvas.drawARGB(80, 102, 204, 255)
        //попытка сделать отрисовку адаптивной.
        nodeRadius = (width + height) / 46F
        stroke.textSize = (width + height) / 88F
        //боковой текст с готовыми значениями
        var i = 20F
        for (e in mainList) {
            canvas.drawText(e.toString(), 0F, i, stroke)
            i += 25
        }
        //начинаем отрисовывать граф.
        drawNode("start", null, (height / 2F), 50F)
        drawGraph(mainList, (height / 2F), 130F)
    }

    /***
     *
     */
    private fun drawGraph(list: List<Pair<Char, String>>, x0: Float, y0: Float, k: Float = 3F) {
        if (list.isEmpty()) {
            return
        }
        val xOffset = nodeRadius * 1.6F
        val yOffset = nodeRadius * 2F
        val y = y0 + yOffset
        //Данный коэффициент влияет на расстояние между соседними элементами.
        //Чем дальше граф идет вниз, тем меньше потомков у детей.
        //Им не нужно большое расстояние между ними.
        val nextK = if (k < 1) k else k / 1.4F
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
}