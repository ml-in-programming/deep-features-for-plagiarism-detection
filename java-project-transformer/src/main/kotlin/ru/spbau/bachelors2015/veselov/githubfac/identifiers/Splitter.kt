package ru.spbau.bachelors2015.veselov.githubfac.identifiers

import java.util.regex.Pattern

object Splitter {
    fun split(identifier: String) : List<String> {
        if (!identifier.isIdentifier()) {
            throw IllegalArgumentException("$identifier is not an identifier")
        }

        val tokens = identifier.split('_')
                               .filter { it.isNotEmpty() }
                               .flatMap { separateLettersAndDigits(it) }

        val result = mutableListOf<String>()
        for (token in tokens) {
            if (token.containsOnlyLetters()) {
                result.addAll(splitLetters(token))
            } else {
                result.add(token)
            }
        }

        return result
    }

    private fun separateLettersAndDigits(string: String) : List<String> {
        if (!string.containsOnlyLettersAndDigits()) {
            throw IllegalArgumentException("$string does not contain only letters and digits")
        }

        val matcher = Pattern.compile("([A-Za-z]+)|([0-9]+)").matcher(string)

        val result = mutableListOf<String>()
        while (!matcher.hitEnd()) {
            matcher.find()
            result.add(matcher.group())
        }

        return result
    }

    private fun splitLetters(letters: String) : List<String> {
        if (!letters.containsOnlyLetters()) {
            throw IllegalArgumentException("$letters does not contain only letters")
        }

        if (letters.isUpperCase()) {
            return listOf(letters.toLowerCase())
        }

        val result = mutableListOf<String>()

        var beginIndex = 0
        for (i in 1..letters.length) {
            if (letters.length == i || letters[i].isUpperCase()) {
                result.add(letters.substring(beginIndex, i).toLowerCase())
                beginIndex = i
            }
        }

        return result
    }
}