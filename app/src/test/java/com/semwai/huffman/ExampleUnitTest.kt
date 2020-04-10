package com.semwai.huffman

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun huffTest1() {
        val myH = Huffman(mapOf('a' to 5, 'b' to 3, 'c' to 1)).getPath()
        assertEquals("11", myH['c'])
        assertEquals("10", myH['b'])
        assertEquals("0", myH['a'])
    }
    @Test
    fun offsetTest1(){
        val myH = Huffman(mapOf('a' to 5, 'b' to 3, 'c' to 1))
        println(myH.getPath())
        val w = offsetMaster(myH.getRootNode())
        for (i in w)
            println(i)
        assertEquals(1f to 1f, w["a"])
        assertEquals(1f to 1f, w["b"])
        assertEquals(1f to 1f, w["c"])
        assertEquals(2f to 2f, w["cba"])
    }
}
