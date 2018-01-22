package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile

class FileObtainer(private val project: Project) {
    fun getAllJavaFiles() : List<VirtualFile> {
        val result: MutableList<VirtualFile> = mutableListOf()

        ProjectFileIndex.SERVICE.getInstance(project).iterateContent {
            if (isJavaFile(it)) {
                result.add(it)
            }

            true
        }

        return result
    }

    private fun isJavaFile(file: VirtualFile) : Boolean {
        return !file.isDirectory && file.extension == "java"
    }
}