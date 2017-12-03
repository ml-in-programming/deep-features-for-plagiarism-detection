package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter

class JavaFile(sourceCode: String) {
    private val context = SourceContext(sourceCode)

    fun fields(): List<JavaField> {
        return context.fields()
    }

    fun localVars(): List<JavaLocalVar> {
        return context.localVars()
    }

    fun methods(): List<JavaMethod> {
        return context.methods()
    }

    fun parameters(): List<JavaParameter> {
        return context.parameters()
    }

    fun printCode() : String {
        return LexicalPreservingPrinter.print(context.unit)
    }
}
