package com.semwai.haffman

import android.content.Context
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

import android.R.attr.y
import android.R.attr.x
import android.content.Intent

import android.graphics.Paint.ANTI_ALIAS_FLAG




class MainActivity : AppCompatActivity() {

    val myMap = mutableMapOf<Char,Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button2.setOnClickListener {
            val letter = inputChar.text.firstOrNull()
            if (letter != null){
                myMap[letter] = myMap.getOrDefault(letter,0) + (count.text.toString().toIntOrNull()?:0)
                list.removeAllViews()
                myMap.toList().reversed().forEach {
                    val t = TextView(this)
                    t.text = "${it.first} - ${it.second}"
                    t.setOnClickListener {
                            btn ->
                        myMap.remove(letter)
                        list.removeView(btn)
                    }
                    list.addView(t)
                }

            }
        }
        drawButton.setOnClickListener {
            val i = Intent(this,drawer::class.java)
            i.putExtra("map", myMap.toString())
            startActivity(i)
        }
    }


}



