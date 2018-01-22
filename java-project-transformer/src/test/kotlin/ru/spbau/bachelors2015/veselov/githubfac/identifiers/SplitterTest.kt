package ru.spbau.bachelors2015.veselov.githubfac.identifiers

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.*
import org.junit.Test

class SplitterTest {
    @Test
    fun singleWord() {
        test("word", "word")
        test("WORD", "word")
        test("Word", "word")
    }

    @Test
    fun snakeNotation() {
        test("one_two", "one", "two")
        test("ONE_TWO", "one", "two")
        test("ONE_TWO_42", "one", "two", "42")
    }

    @Test
    fun camelNotation() {
        test("oneTwo", "one", "two")
        test("oneTwoThree", "one", "two", "three")
        test("OneTwoThree", "one", "two", "three")
        test("oneTwoTH", "one", "two", "t", "h")
        test("One2Three", "one", "2", "three")
        test("One2three", "one", "2", "three")
    }

    @Test
    fun mixedNotations() {
        test("oNe_TWO", "o", "ne", "two")
        test("INVALID_getDamage", "invalid", "get", "damage")
        test("INVALID_GET9000damage", "invalid", "get", "9000", "damage")
    }

    @Test
    fun underscoreAtTheBeginning() {
        test("_INVALID_getDamage", "invalid", "get", "damage")
    }

    private fun test(identifier: String, vararg tokens: String) {
        assertThat(Splitter.split(identifier), `is`(equalTo(tokens.toList())))
    }
}