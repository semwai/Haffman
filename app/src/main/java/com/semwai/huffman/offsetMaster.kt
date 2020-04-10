package com.semwai.huffman
/**
Необходимо найти оптимальное расстояние между нодами.
Если просто взять начальное значение как степень двойки и делить 2 на, то получится слишком много неиспользоемого пространства
Как выход - использовать готовое дерево и расчитывать его ширину как сумму ширин его детей. Если ребенок - конечная буква, то брать единицу.
чтобы избавится от "зигзагов", добавлено дополнительное смещение по Y

 Изначально это был класс. Но по факту у него только одна задача и практичнее было оформить его в виде функции.
*/
fun offsetMaster(inputGraph: Huffman.HNode): Map<String,  Pair<Float,Float>>{
    val output: MutableMap<Huffman.Point, Pair<Float,Float>> = mutableMapOf()
    fun makeWidth(input: Huffman.Point){
        if (output.containsKey(input))
            return
        if (input is Huffman.HChar)
            output[input] =  1f to 1f
        else {
            with(input as Huffman.HNode){
                makeWidth(a)
                makeWidth(b)
                val aa = output[a]!!
                val bb = output[b]!!
                var t = 0f
                if (aa.first == bb.first*2){
                    output[a] = aa.first - 1 to (aa.second + 1)
                    t += 1f
                }
                if (bb.first == aa.first*2){
                    output[b] = bb.first - 1 to (bb.second + 1)
                    t += 1f
                }
                output[input] = (output[a]!!.first + output[b]!!.first) to (1f + t)
            }
        }
    }

    makeWidth(inputGraph)
    return output.map { it.key.chars to it.value }.toMap()
}