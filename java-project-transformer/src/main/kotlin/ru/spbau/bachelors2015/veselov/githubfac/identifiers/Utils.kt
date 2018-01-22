package ru.spbau.bachelors2015.veselov.githubfac.identifiers

fun String.isInSnakeNotation() : Boolean {
    return this.contains('_')
}

fun String.isIdentifier() : Boolean {
    return this.matches(Regex("[A-Za-z_][A-Za-z0-9_]*"))
}

fun String.containsOnlyLettersAndDigits() : Boolean {
    return this.matches(Regex("[A-Za-z0-9]+"))
}

fun String.containsOnlyLetters() : Boolean {
    return this.matches(Regex("[A-Za-z]+"))
}

fun String.hasOnlyLowerLetters() : Boolean {
    return this.matches(Regex("[a-z]+"))
}

fun String.hasOnlyDigits() : Boolean {
    return this.matches(Regex("[0-9]+"))
}

fun String.isUpperCase() : Boolean {
    return this.matches(Regex("[A-Z]+"))
}

fun String.capitalized() : String {
    return this[0].toUpperCase().toString() + this.substring(1)
}