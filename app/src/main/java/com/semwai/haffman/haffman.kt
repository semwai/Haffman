package com.semwai.haffman

class Haffman(str: String) {

    interface Point {
        fun getWeight(): Int
        fun getChars(): Any
    }

    data class HChar(val char: Char, val w: Int) : Point {
        override fun getChars() = char
        override fun getWeight() = w
        override fun toString() = "$char - $w"
    }

    data class HNode(val a: Point, val b: Point) : Point {
        override fun getChars() = listOf(a.getChars()) + listOf(b.getChars())
        override fun getWeight() = a.getWeight() + b.getWeight()
        override fun toString() = "${getChars().joinToString(separator = "")} - ${getWeight()}"
    }

    private val l = mutableListOf<Point>()

    private val outmap = mutableMapOf<Char,String>()
    init {
        str.groupBy { it }.map { it.key to it.value.size }.forEach {
            l.add(HChar(it.first, it.second))
        }
        while (l.size > 1) {
            l.sortBy { it.getWeight() }
            val p1 = l.removeAt(0)
            val p2 = l.removeAt(0)
            l.add(HNode(p1, p2))
        }
        make()
    }

    private fun result(po: Point, path: String): String {
        if (po is HChar){
            val c = po.getChars()
            outmap[c] = path
            return "$c - $path"
        }
        else {
            with(po as HNode) {
                return result(po.a, path + "1") + " " + result(po.b, path + "0")
            }
        }
    }

    fun make() = result(l.first(), "")

    fun getMap(): Map<Char,String> = outmap

    fun toBin(str: String): String{
        return str.map { outmap[it] ?: throw Exception("Letter `$it` dont contains in this table") }.joinToString (separator = "")
    }

    /**
     * input - строка в виде бинарной последовательности
     */
    fun fromBin(str: String): String{
        require(str.all { it == '0' || it == '1' })

        val t = str.iterator()
        var temp = ""
        var out = ""
        while (t.hasNext()){
            temp += t.nextChar()
            val ch = outmap.toList().firstOrNull { it.second == temp }?.first
            if (ch != null){
                out += ch
                temp = ""
            }
        }
        return out

    }
}
