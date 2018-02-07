package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Paths

fun linesOfCode(document: Document) : Int {
    return document.charsSequence.count { it == '\n' }
}

// todo: looks very odd
fun copyFileContentTo(
    directory: VirtualFile,
    file: VirtualFile,
    newName: String,
    project: Project,
    requestor: Any
) {
    /*ApplicationManager.getApplication().runReadAction {
        val copy = Paths.get(directory.path, newName).toFile()
        FileUtils.writeStringToFile(
            copy,
            PsiManager.getInstance(project).findFile(file)!!.text,
            null as Charset?)
    }*/


    ApplicationManager.getApplication().runWriteAction {
        val copy = directory.createChildData(requestor, newName)
        val document = FileDocumentManager.getInstance().getDocument(copy)
        document!!.setText(PsiManager.getInstance(project).findFile(file)!!.text)
    }
}
