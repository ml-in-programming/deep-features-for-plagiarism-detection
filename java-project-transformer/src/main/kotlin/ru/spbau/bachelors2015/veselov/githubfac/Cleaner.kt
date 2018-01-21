package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager

class Cleaner(private val project: Project) {
    fun clean(files: List<VirtualFile>) {
        removeComments(files)
    }

    private fun removeComments(files: List<VirtualFile>) {
        // todo: modify to delete string literals
        files.forEach {
            val psiFile = PsiManager.getInstance(project).findFile(it)
            if (psiFile == null || !(psiFile is PsiJavaFile)) {
                return@forEach
            }

            val comments = mutableListOf<PsiComment>()
            object : JavaRecursiveElementVisitor() {
                override fun visitComment(comment: PsiComment?) {
                    super.visitComment(comment)

                    if (comment != null) {
                        comments.add(comment)
                    }
                }
            }.visitElement(psiFile)

            comments.forEach { it.delete() }
        }
    }
}