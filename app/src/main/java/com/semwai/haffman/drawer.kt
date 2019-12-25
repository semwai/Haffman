package com.semwai.haffman

import android.content.Context
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.support.v4.content.ContextCompat.getSystemService
import android.view.WindowManager
import android.util.DisplayMetrics
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.support.v4.content.ContextCompat.getSystemService
import android.view.Display
import android.R.attr.y
import android.R.attr.x
import android.content.Intent
import android.support.annotation.ColorInt
import android.util.Log
import java.lang.Exception
import java.sql.Time
import kotlin.random.Random


var haffStr = ""

class drawer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var i = intent
        haffStr = i.getStringExtra("map")

        setContentView(DrawView(this))
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        Log.v("Width", "" + width)
        Log.v("height", "" + height)
    }

}


internal class DrawView(context: Context) : View(context) {

    var p: Paint
    var t: Paint
    var nodeRadius = 0F
    var canvas: Canvas? = null

    init {
        p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.color = Color.GREEN
        t = Paint(Paint.ANTI_ALIAS_FLAG)
        t.strokeWidth = 3F
        t.color = Color.BLACK

    }

    override fun onDraw(cnv: Canvas) {
        canvas = cnv
        cnv.rotate(90F)
        cnv.translate(0F, -cnv.width * 1.0F)
        canvas!!.drawARGB(80, 102, 204, 255)
        /*
        // первая линия
        p.color = Color.BLACK
        canvas.drawLine(100F, 100F, 600F, 100F, p)
        // точка отклонения для первой линии
        p.style = Paint.Style.FILL
        p.color = Color.GREEN
        canvas.drawCircle(point1.x.toFloat(), point1.y.toFloat(), 10F, p)
        // квадратичная кривая
        path.reset()
        path.moveTo(100F, 100F)
        path.quadTo(point1.x.toFloat(), point1.y.toFloat(), 600F, 100F)
        p.style = Paint.Style.STROKE
        canvas.drawPath(path, p)
        // вторая линия
        p.color = Color.BLACK
        canvas.drawLine(400F, 400F, 1100F, 400F, p)
        // точки отклонения для второй линии
        p.style = Paint.Style.FILL
        p.color = Color.BLUE
        canvas.drawCircle(point21.x.toFloat(), point21.y.toFloat(), 10F, p)
        canvas.drawCircle(point22.x.toFloat(), point22.y.toFloat(), 10F, p)
        // кубическая кривая
        path.reset()
        path.moveTo(400F, 400F)
        path.cubicTo(point21.x.toFloat(), point21.y.toFloat(), point22.x.toFloat(), point22.y.toFloat(), 1100F, 400F)
        p.style = Paint.Style.STROKE
        canvas.drawPath(path, p)
        p.textSize = 30f
        for (i in 0..100)
        canvas.drawText("hello", 10f, (i*40).toFloat(),p)
        canvas.drawText("width ${canvas.width} height ${canvas.height}", 40f, 40f,p)*/
        nodeRadius = (width + height) / 46F
        t.textSize = (width + height) / 88F

        /*drawNode("abcde", 15, false, 300F, 200F)
        drawNode("ac", 10, true, 150F, 330F)
        drawEdge(300F,200F,150F,330F)*/
        val mlist = Haffman(haffStr).getMap().toList().sortedWith(compareBy({ it.second }))
        var i = 20F
        for (e in mlist) {
            cnv.drawText(e.toString(), 0F, i, t)
            i += 20
        }
        drawNode("start", 0,null, (height / 2F), 50F)
        createGraph(mlist, (height / 2F), 100F)


    }

    /* fun createGraph(list: List<Pair<Char, String>>, x0: Float, y0: Float) {
         val list1 = list.filter { it.second.first() == '1' }
         val list0 = list.filter { it.second.first() == '0' }
         val y = y0 + nodeRadius*2.5F
         if (list1.map { it.first }.isNotEmpty())
             drawNode(list1.map { it.first }.joinToString(""), 0, true, x0 - nodeRadius * 1.3F, y)
         if (list0.map { it.first }.isNotEmpty())
             drawNode(list0.map { it.first }.joinToString(""), 0, false, x0 + nodeRadius * 1.3F, y)
         if (list1.size > 1) {
             try{
                 createGraph(list1.subList(1, list0.size - 1), x0 - nodeRadius * 1.3F, y)
             }
             catch (e: Exception){}
         }
         if (list0.size > 1) {
             try{
                 createGraph(list0.subList(1, list0.size - 1), x0 + nodeRadius * 1.3F, y)
             }
             catch (e: Exception){}
         }


     }*/
    fun createGraph(list: List<Pair<Char, String>>, x0: Float, y0: Float, k: Float = 3F) {
        if (list.isEmpty()) {
            return
        }
        val xOffset = nodeRadius * 1.6F
        val yOffset = nodeRadius * 2F
        val y = y0 + yOffset
        val nextK = if (k < 1) k else k / 1.4F
        val list1 = list.filter { it.second.firstOrNull() == '1' }
        val list0 = list.filter { it.second.firstOrNull() == '0' }

        if (list1.isNotEmpty()) {
            drawNode(list1.map { it.first }.joinToString(""), 0, true, x0 - k*xOffset, y0)
            createGraph(list1.map { it.first to it.second.drop(1) }, x0 - k*xOffset, y, nextK)
            drawEdge(x0, y0 - yOffset, x0 - k*xOffset, y0)
        }

        if (list0.isNotEmpty()) {
            drawNode(list0.map { it.first }.joinToString(""), 0, false, x0 + k*xOffset, y0)
            createGraph(list0.map { it.first to it.second.drop(1) }, x0 + k*xOffset, y, nextK)
            drawEdge(x0, y0 - yOffset, x0 + k*xOffset, y0)
        }
    }

    /*
    рисует отдельную вершину
     */
    fun drawNode(text: String, weight: Int, point: Boolean?, x: Float, y: Float) {
        p.color = Color.BLACK
        canvas!!.drawCircle(x, y, nodeRadius + 2, p)
        p.color = Color.parseColor("#008577")
        canvas!!.drawCircle(x, y, nodeRadius, p)
        canvas!!.drawText(text, x - t.textSize * text.length / 4, y + 4, t)
        //canvas!!.drawText(weight.toString(), x + nodeRadius + 2, y + 4, t)
        if (point != null) {
            canvas!!.drawText(if (point!!) "1" else "0", x - nodeRadius - 15, y + 4, t)
            }

    }

    fun drawEdge(x1: Float, y1: Float, x2: Float, y2: Float) {
        val angle = Math.atan2(y2.toDouble() - y1.toDouble(), x2.toDouble() - x1.toDouble())
        canvas!!.drawLine(
            x1 + nodeRadius * Math.cos(angle).toFloat(),
            y1 + nodeRadius * Math.sin(angle).toFloat(),
            x2 - nodeRadius * Math.cos(angle).toFloat(),
            y2 - nodeRadius * Math.sin(angle).toFloat(),
            t
        )
    }
}