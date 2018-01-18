package ru.spbau.bachelors2015.veselov.githubfac

import org.apache.commons.io.FileUtils
import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

object ResourcesManager {
    private val origin = "origin"

    private val declarations = "declarations"

    private val renamed = "renamed"

    private val shifted = "shifted"

    fun getAllNames() : ArrayList<String> {
        val sourceRoot = Paths.get(javaClass.getResource("/$origin/").toURI())
        val names = ArrayList<String>()

        Files.walkFileTree(sourceRoot, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                if (file.toString().endsWith(".java")) {
                    val fileName = sourceRoot.relativize(file).toString()
                    names.add(fileName.substring(0, fileName.length - ".java".length))
                }

                return FileVisitResult.CONTINUE
            }
        })

        return names
    }

    fun getPathToSubFolder(name: String) : Path {
        return Paths.get(javaClass.getResource("/$name").toURI())
    }

    fun getOrigin(name: String) : String {
        return getFromFolder(name, origin, ".java") ?: throw IllegalArgumentException()
    }

    fun getDeclarations(name: String) : List<String>? {
        return getFromFolder(name, declarations, "")?.split('\n')
    }

    fun getRenamed(name: String) : String? {
        return getFromFolder(name, renamed, ".java")
    }

    fun getShifted(name: String) : String? {
        return getFromFolder(name, shifted, ".java")
    }

    private fun getFromFolder(fileName: String, folderName: String, suffix: String) : String? {
        return fileToString("/$folderName/$fileName$suffix")
    }

    private fun fileToString(fileName: String) : String? {
        val url = javaClass.getResource(fileName)?.file ?: return null
        return FileUtils.readFileToString(Paths.get(url).toFile(), null as Charset?)
    }
}