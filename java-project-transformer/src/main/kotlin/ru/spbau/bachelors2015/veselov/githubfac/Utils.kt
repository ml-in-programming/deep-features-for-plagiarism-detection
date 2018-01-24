package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager

fun linesOfCode(document: Document) : Int {
    return document.charsSequence.count { it == '\n' }
}

// todo: looks very odd
fun copyFileTo(directory: VirtualFile, file: VirtualFile, project: Project, requestor: Any) {
    ApplicationManager.getApplication().runWriteAction {
        val copy = directory.createChildData(requestor, file.name)
        val document = FileDocumentManager.getInstance().getDocument(copy)
        document!!.setText(PsiManager.getInstance(project).findFile(file)!!.text)
    }
}