package ru.spbau.bachelors2015.veselov.githubfac

object Log {
    fun write(message: String) {
        println(message)
    }

    fun err(message: String) {
        System.err.println(message)
    }
}