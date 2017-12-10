package ru.spbau.bachelors2015.veselov.githubfac

import org.apache.commons.io.FileUtils
import java.nio.charset.Charset
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Invalid number of arguments. Should be 1.")
        return
    }

    val pathToFile = Paths.get(args[0])
    val javaFile = JavaFile(FileUtils.readFileToString(pathToFile.toFile(), null as Charset?))

    for ((ctr, declaration) in javaFile.declarations().withIndex()) {
        javaFile.rename(declaration, "d${ctr + 1}")
    }

    println(javaFile.printCode())
}
