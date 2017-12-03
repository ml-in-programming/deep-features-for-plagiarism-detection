package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.ast.body.MethodDeclaration

class JavaMethod(private val context: SourceContext,
                 private val methodDeclaration: MethodDeclaration) {
    fun simpleName(): String {
        return methodDeclaration.getNameAsString()
    }

    fun renameTo(newName: String) {
        context.rename(methodDeclaration, newName)
    }
}