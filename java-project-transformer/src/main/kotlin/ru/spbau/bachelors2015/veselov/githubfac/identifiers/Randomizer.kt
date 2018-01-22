package ru.spbau.bachelors2015.veselov.githubfac.identifiers

import ru.spbau.bachelors2015.veselov.githubfac.Log
import java.util.*

object Randomizer {
    fun randomize(tokens: List<String>) : List<String> {
        return tokens.map { randomize(it) }
    }

    private fun randomize(token: String) : String {
        val index = Random().nextInt(token.length)
        val builder = StringBuilder(token)
        builder[index] = nextChar(builder[index])

        return builder.toString()
    }

    private fun nextChar(char: Char) : Char {
        if (char.isDigit()) {
            return nextInBetween(char, '0', '9')
        }

        if (char in 'a'..'z') {
            return nextInBetween(char, 'a', 'z')
        }

        throw RuntimeException()
    }

    private fun nextInBetween(char: Char, first: Char, last: Char) : Char {
        return first + ((char - first) + 1) % (last - first + 1)
    }
}