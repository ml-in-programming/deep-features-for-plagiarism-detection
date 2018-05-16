package ru.spbau.bachelors2015.veselov.githubfac

import org.kohsuke.github.GHFileNotFoundException
import org.kohsuke.github.GitHub
import ru.spbau.bachelors2015.veselov.githubfac.model.JavaSourceFile
import java.nio.file.Paths

class NoSuchRepositoryException : Exception()

class NoSuchPrException : Exception()

class PullRequestSupplier(prId: PullRequestIdentifier) : JavaSourcesSupplier {
    private val iterable: Iterable<JavaSourceFile>

    init {
        val repository = try {
            github.getRepository(prId.repositoryIdentifier)
        } catch (_: GHFileNotFoundException) {
            throw NoSuchRepositoryException()
        }

        val pullRequest = try {
            repository.getPullRequest(prId.pullRequestId)
        } catch (_: GHFileNotFoundException) {
            throw NoSuchPrException()
        }

        val commitSha = pullRequest.head.sha

        iterable = pullRequest.listFiles().asList().filter {
            Paths.get(it.rawUrl.path).getName(3).toString() == commitSha
        }.map {
            JavaSourceFile(
                it.rawUrl.readText(),
                it.blobUrl.toString()
            )
        }
    }

    override fun iterator(): Iterator<JavaSourceFile> {
        return iterable.iterator()
    }

    private companion object {
        val github = GitHub.connect()
    }
}
