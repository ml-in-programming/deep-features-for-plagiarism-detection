package ru.spbau.bachelors2015.veselov.githubfac

import com.xenomachina.argparser.ArgParser


enum class Operation {
    ADD, FIND_SIMILARITIES
}

fun main(args: Array<String>) {
    val parsedArgs = ArgParser(args).parseInto(::Arguments)

    val client = Client()

    parsedArgs.run {
        val prIdentifier = PullRequestIdentifier(resource)
        val sources = PullRequestSupplier(prIdentifier)

        // val pathToResource = Paths.get(resource)

        when (runMode) {
            Operation.ADD -> client.addFiles(sources)

            Operation.FIND_SIMILARITIES -> client.findSimilarities(sources)
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
