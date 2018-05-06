package ru.spbau.bachelors2015.veselov.githubfac

import com.xenomachina.argparser.ArgParser
import java.nio.file.Paths

enum class Operation {
    ADD, FIND_SIMILARITIES
}

fun main(args: Array<String>) {
    val parsedArgs = ArgParser(args).parseInto(::Arguments)

    parsedArgs.run {
        val model = JavaPlagiarismDetector(Paths.get("", "model"))

        when (runMode) {
            Operation.ADD -> {
                val file = JavaSourceFile(Paths.get(resource))
                file.splitOnMethods().forEach {
                    model.addJavaCodeSnippet(it)
                }
            }

            Operation.FIND_SIMILARITIES -> {
                val file = JavaSourceFile(Paths.get(resource))
                file.splitOnMethods().forEach {
                    model.findSimilarSnippets(it)
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
