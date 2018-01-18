package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.ui.Messages
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class Transformer(val project: Project) {
    fun perform() {
        val trasformationDirectory = Paths.get(project.baseDir.canonicalPath).resolve("transformation").toFile()
        if (trasformationDirectory.exists()) {
            // FileUtils.deleteDirectory(new File("directory"));
            trasformationDirectory.delete()
        }

        trasformationDirectory.mkdir()

        /*ProjectFileIndex.SERVICE.getInstance(project).iterateContent {
            TODO("not implemented")
        }*/
    }
}
