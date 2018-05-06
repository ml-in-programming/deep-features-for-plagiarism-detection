package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.xenomachina.argparser.ArgParser
import org.apache.commons.io.FileUtils.toFile
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes


enum class Operation {
    ADD, FIND_SIMILARITIES
}

fun main(args: Array<String>) {
    val parsedArgs = ArgParser(args).parseInto(::Arguments)

    parsedArgs.run {
        val model = JavaPlagiarismDetector(Paths.get("", "model"))

        val pathToResource = Paths.get(resource)
        val typeSolver = JavaParserTypeSolver(pathToResource)
        val javaFiles = mutableListOf<JavaSourceFile>()

        Files.walkFileTree(pathToResource,
            object : SimpleFileVisitor<Path>() {
                override fun visitFile(
                    path: Path,
                    attrs: BasicFileAttributes
                ): FileVisitResult {
                    if (path.toFile().extension == "java") {
                        javaFiles.add(JavaSourceFile(path, typeSolver))
                    }

                    return FileVisitResult.CONTINUE
                }
            }
        )

        when (runMode) {
            Operation.ADD -> {
                javaFiles.forEach {
                    it.splitOnMethods().forEach {
                        model.addJavaCodeSnippet(it)
                    }
                }
            }

            Operation.FIND_SIMILARITIES -> {
                javaFiles.forEach {
                    it.splitOnMethods().forEach {
                        model.findSimilarSnippets(it)
                    }
                }
            }
        }
    }
}

class Arguments(parser: ArgParser) {
    val runMode by parser.mapping(
        "--add" to Operation.ADD,
        "--find-similarities" to Operation.FIND_SIMILARITIES,
        help = "Operation to execute."
    )

    val resource by parser.positional(
        "Resource to operate on: file, pull-request, repository."
    )
}
