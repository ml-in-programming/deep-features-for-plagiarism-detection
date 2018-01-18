package ru.spbau.bachelors2015.veselov.githubfac

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*

fun getAllJarsInDir(pathToDir: Path) : List<Path> {
    val result = arrayListOf<Path>()

    Files.walkFileTree(pathToDir,
        object : SimpleFileVisitor<Path>() {
            override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
                if (path.toString().toLowerCase().endsWith(".jar")) {
                    result.add(path)
                }

                return FileVisitResult.CONTINUE
            }
        }
    )

    return result
}

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Invalid number of arguments. Should be 3.")
        return
    }

    val pathToFile = Paths.get(args[0])
    val javaProject = JavaProject(
        pathToFile,
        args.get(1).let { getAllJarsInDir(Paths.get(it)) }
    )

    val longFiles = javaProject.files.filter { it.linesOfCode() >= 200 }

    val javaFile = javaProject.files.filter { it.path.toString() == args[2] }.first()
    println(javaFile.path)

    javaFile.setupPrinting()
    val declarations = javaProject.declarations()
    println("Found ${declarations.size} declarations")

    for ((ctr, declaration) in declarations.withIndex()) {
        // javaFile.rename(declaration, "__d${ctr + 1}")
        javaFile.usages(declaration)
    }

    println("=".repeat(80))
    println(javaFile.printCode())
}
