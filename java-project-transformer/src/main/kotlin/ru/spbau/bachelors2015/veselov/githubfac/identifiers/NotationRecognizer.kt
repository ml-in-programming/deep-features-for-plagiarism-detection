package ru.spbau.bachelors2015.veselov.githubfac.identifiers

object NotationRecognizer {
    fun recognize(identifier: String) : Notation {
        if (!identifier.isIdentifier()) {
            throw IllegalArgumentException("$identifier is not an identifier")
        }

        if (isUpperSnake(identifier)) {
            return Notation.UPPER_SNAKE
        }

        if (!identifier.containsOnlyLettersAndDigits()) {
            return Notation.LOW_CAMEL
        }

        if (identifier[0].isUpperCase()) {
            return Notation.BIG_CAMEL
        }

        return Notation.LOW_CAMEL
    }

    private fun isUpperSnake(identifier: String) : Boolean {
        return identifier.matches(Regex("[A-Z0-9_]+"))
    }
}