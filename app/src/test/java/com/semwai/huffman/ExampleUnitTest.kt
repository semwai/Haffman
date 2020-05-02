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
        val myH = Huffman(mapOf('A' to 15, 'B' to 7, 'C' to 6, 'D' to 6, 'E' to 5)).getPath()
        assertEquals("1", myH['A'])
        assertEquals("000", myH['B'])
        assertEquals("011", myH['E'])
    }
    @Test
    fun offsetTest1(){
        val myH = Huffman(mapOf('a' to 5, 'b' to 3, 'c' to 1))
        println(myH.getPath())
        val w = offsetMaster(myH.rootNode)
        for (i in w)
            println(i)
        assertEquals(1f to 1f, w["a"])
        assertEquals(1f to 1f, w["b"])
        assertEquals(1f to 1f, w["c"])
        assertEquals(2f to 2f, w["cba"])
    }
}
