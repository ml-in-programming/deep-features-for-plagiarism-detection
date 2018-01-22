package ru.spbau.bachelors2015.veselov.githubfac.identifiers

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.*
import org.junit.Test

class NotationRecognizerTest {
    @Test
    fun upperSnakeNotation() {
        test("ONE", Notation.UPPER_SNAKE)
        test("ONE_TWO", Notation.UPPER_SNAKE)
        test("ONE_TWO_THREE", Notation.UPPER_SNAKE)
        test("ONE_2_THREE", Notation.UPPER_SNAKE)
    }

    @Test
    fun lowCamelNotation() {
        test("one", Notation.LOW_CAMEL)
        test("oneTwo", Notation.LOW_CAMEL)
        test("oneTwoThree", Notation.LOW_CAMEL)
        test("one2Three", Notation.LOW_CAMEL)
    }

    @Test
    fun bigCamelNotation() {
        test("One", Notation.BIG_CAMEL)
        test("OneTwo", Notation.BIG_CAMEL)
        test("OneTwoThree", Notation.BIG_CAMEL)
        test("One2Three", Notation.BIG_CAMEL)
    }

    private fun test(identifier: String, notation: Notation) {
        assertThat(NotationRecognizer.recognize(identifier), `is`(equalTo(notation)))
    }
}