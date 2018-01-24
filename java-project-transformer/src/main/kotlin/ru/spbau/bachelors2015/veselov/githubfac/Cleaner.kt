package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*

class Cleaner(private val project: Project) {
    fun clean(files: List<VirtualFile>) {
        files.forEach {
            val psiFile = PsiManager.getInstance(project).findFile(it)
            if (psiFile == null || !(psiFile is PsiJavaFile)) {
                return@forEach
            }

            val document = FileDocumentManager.getInstance().getDocument(it)
            if (document == null) {
                return@forEach
            }

            PsiDocumentManager.getInstance(project).commitDocument(document)

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


            WriteCommandAction.runWriteCommandAction(project, {
                comments.forEach {
                    it.delete()
                }
            })

            WriteCommandAction.runWriteCommandAction(project, {
                literals.forEach {
                    val value = it.value
                    if (value is String) {
                        val factory = JavaPsiFacade.getInstance(project).elementFactory
                        val replacement = factory.createExpressionFromText("\"\"", it.context)
                        it.replace(replacement)
                    }
                }
            })
        }
    }
}