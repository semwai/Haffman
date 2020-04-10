package com.semwai.huffman

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import java.lang.StringBuilder
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    //данный тип можно передать в Intent и не придется делать свой Parcelable\Serializable
    private val myMap = linkedMapOf<Char, Int>()

    @SuppressLint("SetTextI18n")
    private fun letterButton(i: Char, c: Int): Button {
        val btn = Button(this)
        btn.text = "$i - $c"
        btn.setOnClickListener {
            myMap.remove(i)
            list.removeView(btn)
        }
        return btn
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generatorButton.setOnClickListener {
            list.removeAllViews()
            myMap.clear()
            val chars = ('a'..'z')
            for (i in chars) {
                val c = Random.nextInt(5, 500)
                myMap[i] = c
                list.addView(letterButton(i,c))
            }
        }
        addButton.setOnClickListener {
            val letter = inputChar.text.firstOrNull()
            if (letter != null) {
                myMap[letter] =
                    myMap.getOrDefault(letter, 1) + (count.text.toString().toIntOrNull() ?: 0)
                list.removeAllViews()
                myMap.toList().reversed().forEach {
                    list.addView(letterButton(it.first,it.second))
                }
            }
        }
        drawButton.setOnClickListener {
            val i = Intent(this, Drawer::class.java)

            if (myMap.size < 2) {
                Toast.makeText(this, R.string.need_data, Toast.LENGTH_LONG).show()
            } else {
                i.putExtra("map", myMap)
                startActivity(i)
            }
        }
    }
}



