package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter

class JavaFile(sourceCode: String) {
    private val context = SourceContext(sourceCode)

    fun fields(): List<JavaField> {
        return context.fields()
    }

    fun printCode() : String {
        return LexicalPreservingPrinter.print(context.unit)
    }
}
