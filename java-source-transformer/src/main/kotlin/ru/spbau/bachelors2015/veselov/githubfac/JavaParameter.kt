package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.ast.body.Parameter

class JavaParameter(private val context: SourceContext,
                    private val parameter: Parameter) {
    fun simpleName(): String {
        return parameter.getNameAsString()
    }

    fun renameTo(newName: String) {
        context.rename(parameter, newName)
    }
}