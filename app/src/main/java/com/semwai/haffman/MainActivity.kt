package com.semwai.haffman


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.widget.Button


class MainActivity : AppCompatActivity() {

    val myMap = mutableMapOf<Char,Int>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generator.setOnClickListener {


            list.removeAllViews()
            myMap.clear()
            val chars = 'a'..'z'
            val numbers = 1..20
            for (i in  0..20){
                val l = chars.shuffled().first()
                val c = numbers.shuffled().first()
                myMap[l] = c
                val t = Button(this)
                t.text = "${c} - ${l}"
                t.setOnClickListener {
                        btn ->
                    myMap.remove(l)
                    list.removeView(btn)


                }
                list.addView(t)
            }
        }
        button2.setOnClickListener {
            val letter = inputChar.text.firstOrNull()
            if (letter != null){
                myMap[letter] = myMap.getOrDefault(letter,0) + (count.text.toString().toIntOrNull()?:0)
                list.removeAllViews()
                myMap.toList().reversed().forEach {
                    val t = Button(this)
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



