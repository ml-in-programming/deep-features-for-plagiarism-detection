package ru.spbau.bachelors2015.veselov.githubfac

import org.apache.commons.io.IOUtils
import java.io.PrintWriter
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths

class JavaPlagiarismDetector(private val folder: Path) {
    private val addScriptName = "add_snippet.py"

    private val findScriptName = "find_similarities.py"

    fun addJavaCodeSnippet(snippet: JavaCodeSnippet) {
        val process = ProcessBuilder(
            "python3",
            Paths.get(folder.toString(), addScriptName).toString()
        ).start()

        PrintWriter(process.outputStream).use {
            it.print(snippet.description)
            it.print('\u0000')
            it.print(snippet.code)
        }

        if (process.waitFor() != 0) {
            System.err.println(IOUtils.toString(process.errorStream, Charset.defaultCharset()))
            throw RuntimeException("Python script has failed")
        }
    }

    fun findSimilarSnippets(snippet: JavaCodeSnippet) {
        // TODO: both functions look similar

        val process = ProcessBuilder(
            "python3",
            Paths.get(folder.toString(), findScriptName).toString()
        ).start()

        PrintWriter(process.outputStream).use {
            it.print(snippet.code)
        }

        if (process.waitFor() != 0) {
            System.err.println(IOUtils.toString(process.errorStream, Charset.defaultCharset()))
            throw RuntimeException("Python script has failed")
        }

        println(IOUtils.toString(process.inputStream, Charset.defaultCharset()))
    }
}
