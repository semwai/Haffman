package com.semwai.haffman

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val myH = Haffman("aaaaaaaabbbc").getPath()
        assertEquals("11", myH['c'])
        assertEquals("10", myH['b'])
        assertEquals("0", myH['a'])
    }
}
