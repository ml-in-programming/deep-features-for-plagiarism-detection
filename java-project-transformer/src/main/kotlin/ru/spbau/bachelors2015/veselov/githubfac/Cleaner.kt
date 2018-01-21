package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*

class Cleaner(private val project: Project) {
    // todo: modify to delete string literals
    fun clean(files: List<VirtualFile>) {
        files.forEach {
            val psiFile = PsiManager.getInstance(project).findFile(it)
            if (psiFile == null || !(psiFile is PsiJavaFile)) {
                return@forEach
            }

            val comments = mutableListOf<PsiComment>()
            val literals = mutableListOf<PsiLiteralExpression>()

            object : JavaRecursiveElementVisitor() {
                override fun visitComment(comment: PsiComment?) {
                    super.visitComment(comment)

                    if (comment != null) {
                        comments.add(comment)
                    }
                }

                override fun visitLiteralExpression(expression: PsiLiteralExpression?) {
                    super.visitLiteralExpression(expression)

                    if (expression != null) {
                        literals.add(expression)
                    }
                }
            }.visitElement(psiFile)

            comments.forEach { it.delete() }

            literals.forEach {
                val value = it.value
                if (value is String) {
                    val factory = JavaPsiFacade.getInstance(project).elementFactory
                    val replacement = factory.createExpressionFromText("\"\"", it.context)
                    it.replace(replacement)
                }
            }
        }
    }
}