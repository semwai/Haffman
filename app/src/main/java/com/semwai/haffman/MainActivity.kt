package com.semwai.haffman

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.widget.Button
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val myMap = mutableMapOf<Char, Int>()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generatorButton.setOnClickListener {
            list.removeAllViews()
            myMap.clear()
            val chars = ('a'..'z').toMutableList()
            val numbers = 1..40
            for (i in 0..5) {
                val l = chars.shuffled().first()
                chars.remove(l)
                val c = numbers.shuffled().first()
                myMap[l] = c
                val t = Button(this)
                t.text = "$l - $c"
                t.setOnClickListener { btn ->
                    myMap.remove(l)
                    list.removeView(btn)
                }
                list.addView(t)
            }
        }
        addButton.setOnClickListener {
            val letter = inputChar.text.firstOrNull()
            if (letter != null) {
                myMap[letter] =
                    myMap.getOrDefault(letter, 0) + (count.text.toString().toIntOrNull() ?: 0)
                list.removeAllViews()
                myMap.toList().reversed().forEach {
                    val t = Button(this)
                    t.text = "${it.first} - ${it.second}"
                    t.setOnClickListener { btn ->
                        myMap.remove(letter)
                        list.removeView(btn)
                    }
                    list.addView(t)
                }
            }
        }
        drawButton.setOnClickListener {
            val i = Intent(this, Drawer::class.java)
            val s = StringBuilder()
            myMap.forEach {
                s.append("${it.key}".repeat(it.value))
            }
            i.putExtra("map", s.toString())
            startActivity(i)
        }
    }
}



