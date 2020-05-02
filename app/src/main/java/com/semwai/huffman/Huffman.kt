package com.semwai.huffman

import java.util.*
import kotlin.Comparator

class Huffman(input: Map<Char, Int>) {
    //Элемент дерева - либо конечный узел, либо узел, хранящий другие узлы.
    interface Point {
        val weight: Int
        val chars: String
    }

    class HChar(private val char: Char, override val weight: Int) : Point {
        override fun toString() = "$char - $weight"
        override val chars: String = "$char"
    }

    class HNode(val a: Point, val b: Point) : Point {
        override val chars: String = a.chars + b.chars
        override val weight = a.weight + b.weight
        override fun toString() = "$chars - $weight"
    }


    /**
     * Хранит готовую таблицу вида буква - бинарный код (путь)
     */
    private val values = mutableMapOf<Char, String>()

    //чтобы не нарушать целостности класса и не возвращать MutableMap
    fun getPath(): Map<Char, String> = values

    lateinit var rootNode: HNode

    init {
        val inputNodes = PriorityQueue<Point>(Comparator { o1, o2 -> o1.weight - o2.weight })
        input.forEach {
            inputNodes.add(HChar(it.key, it.value))
        }
        while (inputNodes.size > 1) {
            val p1 = inputNodes.poll()
            val p2 = inputNodes.poll()
            inputNodes.add(HNode(p1, p2))
        }
        make(inputNodes.first())
        rootNode = inputNodes.first() as HNode
    }

    private fun make(po: Point, path: String = "") {
        if (po is HChar) {
            val c = po.chars
            values[c.first()] = path
        } else {
            with(po as HNode) {
                make(a, path + "1")
                make(b, path + "0")
            }
        }
    }
}