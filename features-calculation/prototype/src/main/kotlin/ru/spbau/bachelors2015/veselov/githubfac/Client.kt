package ru.spbau.bachelors2015.veselov.githubfac

import ru.spbau.bachelors2015.veselov.githubfac.model.JavaPlagiarismDetector
import ru.spbau.bachelors2015.veselov.githubfac.model.JavaSourceFile
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

class Client {
    private val model = JavaPlagiarismDetector(Paths.get("", "model"))

    fun addFiles(sources: JavaSourcesSupplier) {
        sources.forEach {
            println("Adding ${it.fileDescription}")
            it.splitOnMethods().forEach {
                model.addJavaCodeSnippet(it)
            }
        }
    }

    fun findSimilarities(sources: JavaSourcesSupplier) {
        sources.forEach {
            it.splitOnMethods().forEach {
                println(it.description)
                model.findSimilarSnippets(it)
            }
        }
    }

    // TODO: move to special supplier
    private fun getAllJavaSources(path: Path): List<JavaSourceFile> {
        val javaFiles = mutableListOf<JavaSourceFile>()

        Files.walkFileTree(path,
            object : SimpleFileVisitor<Path>() {
                override fun visitFile(
                        path: Path,
                        attrs: BasicFileAttributes
                ): FileVisitResult {
                    if (path.toFile().extension == "java") {
                        javaFiles.add(JavaSourceFile(path))
                    }

                    return FileVisitResult.CONTINUE
                }
            }
        )

        return javaFiles
    }
}
