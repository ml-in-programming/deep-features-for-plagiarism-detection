package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Paths
import java.util.Collections.shuffle

class Transformer(private val project: Project) {
    val log: Log = Log()

    private var transformationDirectory: VirtualFile

    private var originalSubdirectory: VirtualFile

    private var transformedSubdirectory: VirtualFile

    init {
        val transformationName = "transformation"

        val existedDir = LocalFileSystem.getInstance().findFileByIoFile(
            Paths.get(project.baseDir.canonicalPath).resolve(transformationName).toFile()
        )

        existedDir?.delete(this)

        transformationDirectory = project.baseDir.createChildDirectory(
            this,
            transformationName
        )

        originalSubdirectory = transformationDirectory.createChildDirectory(
            this,
            "original"
        )

        transformedSubdirectory = transformationDirectory.createChildDirectory(
            this,
            "transformed"
        )
    }

    fun perform() {
        val appropriateFiles = getAppropriateFiles()
        val sampleFiles = getRandomSample(appropriateFiles)

        for (file in sampleFiles) {
            file.copy(this, originalSubdirectory, file.name)
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

        log.write("${result.size} good java files detected")
        for (file in result) {
            log.write(file.path)
        }

        return result
    }

    private fun getRandomSample(files: List<VirtualFile>) : List<VirtualFile> {
        val list = files.toMutableList()
        shuffle(list)

        val result = list.take(10)

        log.write("${result.size} files in sample")
        for (file in result) {
            log.write(file.path)
        }

        return result
    }
}
