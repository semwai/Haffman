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
import android.support.annotation.ColorInt
import android.util.Log


class drawer : AppCompatActivity() {
    var i = intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
        nodeRadius = (cnv.width + cnv.height) / 35F
        t.textSize = (cnv.width + cnv.height) / 70F

        drawNode("abcde", 15, false, 100F, 200F)
        drawNode("ac", 10, true, 150F, 330F)
        drawEdge(100F,200F,150F,330F)
    }

    /*
    рисует отдельную вершину
     */
    fun drawNode(text: String, weight: Int, point: Boolean, x: Float, y: Float) {
        p.color = Color.BLACK
        canvas!!.drawCircle(x, y, nodeRadius + 2, p)
        p.color = Color.parseColor("#008577")
        canvas!!.drawCircle(x, y, nodeRadius, p)
        canvas!!.drawText(text, x - t.textSize * text.length / 4, y + 4, t)
        canvas!!.drawText(weight.toString(), x + nodeRadius + 2, y + 4, t)
        canvas!!.drawText(if (point) "1" else "0", x - nodeRadius - 15, y + 4, t)
    }

    fun drawEdge(x1: Float, y1: Float, x2: Float, y2: Float) {
        val angle = Math.atan2(y2.toDouble() - y1.toDouble(), x2.toDouble() - x1.toDouble())
        canvas!!.drawLine(
            x1 + nodeRadius*Math.cos(angle).toFloat(),
            y1 + nodeRadius*Math.sin(angle).toFloat(),
            x2 - nodeRadius*Math.cos(angle).toFloat(),
            y2 - nodeRadius*Math.sin(angle).toFloat(),
            t)
    }
}