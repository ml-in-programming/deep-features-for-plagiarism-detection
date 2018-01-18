package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VirtualFile
import org.apache.commons.io.FileUtils
import java.nio.file.Paths

class Transformer(private val project: Project) {
    val log: Log = Log()

    fun perform() {
        val transformationDirectory = Paths.get(project.baseDir.canonicalPath).resolve("transformation").toFile()
        if (transformationDirectory.exists()) {
            FileUtils.deleteDirectory(transformationDirectory)
        }

        transformationDirectory.mkdir()

        val appropriateFiles = getAppropriateFiles()

        log.write("${appropriateFiles.size} good java files detected")
        for (file in appropriateFiles) {
            log.write(file.path)
        }
    }

    private fun getAppropriateFiles() : List<VirtualFile> {
        val result: MutableList<VirtualFile> = mutableListOf()

        ProjectFileIndex.SERVICE.getInstance(project).iterateContent {
            if (FileChecker.isAppropriate(it)) {
                result.add(it)
            }

            return@iterateContent true
        }

        return result
    }
}
