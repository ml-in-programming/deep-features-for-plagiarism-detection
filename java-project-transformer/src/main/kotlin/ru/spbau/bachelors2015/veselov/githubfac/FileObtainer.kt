package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile

class FileObtainer(private val project: Project) {
    fun getFiles() : List<VirtualFile> {
        val result: MutableList<VirtualFile> = mutableListOf()

        ProjectFileIndex.SERVICE.getInstance(project).iterateContent {
            if (isProbablyAppropriate(it)) {
                result.add(it)
            }

            true
        }

        return result
    }

    private fun isProbablyAppropriate(file: VirtualFile) : Boolean {
        if (file.isDirectory || file.extension != "java") {
            return false
        }

        val document = FileDocumentManager.getInstance().getDocument(file)

        return document != null && linesOfCode(document) >= lowerBoundOnLOC
    }
}