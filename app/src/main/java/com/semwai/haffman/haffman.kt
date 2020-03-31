package com.semwai.haffman

//К сожаления андроид не позволяет передавать все типы в активити, поэтому мапа из букв хранится
//в виде повторяющихся букв, идущих друг за другом. К примеру, 'a' to 5, 'b' to 3 = aaaaabbb
//это не должно повлиять на произволительность, но это просто немного костыльно.
class Haffman(str: String) {
    //Элемент дерева - либо конечный узел, либо узел, хранящий другие узлы.
    interface Point {
        fun getWeight(): Int
        fun getChars(): Any
    }

    class HChar(private val char: Char, private val w: Int) : Point {
        override fun getChars() = char
        override fun getWeight() = w
        override fun toString() = "$char - $w"
    }

    class HNode(val a: Point, val b: Point) : Point {
        override fun getChars() = listOf(a.getChars()) + listOf(b.getChars())
        override fun getWeight() = a.getWeight() + b.getWeight()
        override fun toString() = "${getChars().joinToString(separator = "")} - ${getWeight()}"
    }

    //входные ноды
    private val inputNodes = mutableListOf<Point>()

    /**
     * Хранит готовую таблицу вида буква - бинарный код
     */
    val values = mutableMapOf<Char, String>()

    init {
        str.groupBy { it }.map { it.key to it.value.size }.forEach {
            inputNodes.add(HChar(it.first, it.second))
        }
        while (inputNodes.size > 1) {
            inputNodes.sortBy { it.getWeight() }
            val p1 = inputNodes.removeAt(0)
            val p2 = inputNodes.removeAt(0)
            inputNodes.add(HNode(p1, p2))
        }
        result(inputNodes.first(), "")
    }

    private fun result(po: Point, path: String): String {
        if (po is HChar) {
            val c = po.getChars()
            values[c] = path
            return "$c - $path"
        } else {
            with(po as HNode) {
                return result(po.a, path + "1") + " " + result(po.b, path + "0")
            }
        }
    }
}
