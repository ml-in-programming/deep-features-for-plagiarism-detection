package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile

object FileChecker {
    private const val lowerBoundOnLOC = 200

    fun isProbablyAppropriate(file: VirtualFile) : Boolean {
        if (file.isDirectory || file.extension != "java") {
            return false
        }

        val document = FileDocumentManager.getInstance().getDocument(file)

        return document != null && linesOfCode(document) >= lowerBoundOnLOC
    }

    fun isAppropriate(file: VirtualFile) : Boolean {
        val document = FileDocumentManager.getInstance().getDocument(file)

        return document != null // todo: && linesOfCode(document) in 400..800
    }

    private fun linesOfCode(document: Document) : Int {
        return document.charsSequence.count { it == '\n' }
    }
}
