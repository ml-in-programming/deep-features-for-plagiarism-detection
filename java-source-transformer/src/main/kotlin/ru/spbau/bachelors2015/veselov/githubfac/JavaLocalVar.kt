package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.ast.body.VariableDeclarator

class JavaLocalVar(private val context: SourceContext,
                   private val variableDeclarator: VariableDeclarator) {
    fun simpleName(): String {
        return variableDeclarator.getNameAsString()
    }

    fun renameTo(newName: String) {
        context.rename(variableDeclarator, newName)
    }
}