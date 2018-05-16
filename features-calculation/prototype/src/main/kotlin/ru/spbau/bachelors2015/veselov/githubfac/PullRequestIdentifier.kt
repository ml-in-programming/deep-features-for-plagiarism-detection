package ru.spbau.bachelors2015.veselov.githubfac

import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Paths

class MalformedPrIdentifierException : Exception()

class PullRequestIdentifier(urlString: String) {
    val repositoryIdentifier: String

    val pullRequestId: Int

    init {
        val url = try {
            URL(urlString)
        } catch (_: MalformedURLException) {
            throw MalformedPrIdentifierException()
        }

        if (url.host != "github.com") {
            throw MalformedPrIdentifierException()
        }

        val urlPath = Paths.get(url.file)
        if (urlPath.nameCount != 4) {
            throw MalformedPrIdentifierException()
        }

        if (urlPath.getName(2).toString() != "pull") {
            throw MalformedPrIdentifierException()
        }

        repositoryIdentifier =
            urlPath.getName(0).toString() + '/' + urlPath.getName(1).toString()

        pullRequestId = try {
            urlPath.getName(3).toString().toInt()
        } catch (_: NumberFormatException) {
            throw MalformedPrIdentifierException()
        }
    }
}
