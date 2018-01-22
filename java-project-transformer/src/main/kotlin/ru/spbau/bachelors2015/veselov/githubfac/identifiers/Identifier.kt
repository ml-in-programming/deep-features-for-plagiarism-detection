package ru.spbau.bachelors2015.veselov.githubfac.identifiers

class Identifier(projectIdentifier: String) {
    private val tokens: List<String> = Randomizer.randomize(Splitter.split(projectIdentifier))

    fun toSameNotationAs(identifier: String) : String {
        return when (NotationRecognizer.recognize(identifier)) {
            Notation.UPPER_SNAKE -> toSnakeNotation()
            Notation.BIG_CAMEL -> toBigCamelNotation()
            Notation.LOW_CAMEL -> toLowCamelNotation()
        }
    }

    fun toSnakeNotation() : String {
        return tokens.joinToString("_") { it.toUpperCase() }
    }

    fun toBigCamelNotation() : String {
        return tokens.joinToString("") { it.capitalized() }
    }

    fun toLowCamelNotation() : String {
        return tokens[0] + tokens.subList(1, tokens.size).joinToString("") { it.capitalized() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Identifier

        if (tokens != other.tokens) return false

        return true
    }

    override fun hashCode(): Int {
        return tokens.hashCode()
    }

    override fun toString(): String {
        return "Identifier(tokens=$tokens)"
    }
}
