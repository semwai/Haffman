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
    var path: Path
    var point1: Point
    var point21: Point
    var point22: Point

    init {
        p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.setStrokeWidth(3F)
        path = Path()

        point1 = Point(200, 300)
        point21 = Point(500, 600)
        point22 = Point(900, 200)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawARGB(80, 102, 204, 255)


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
    }
}