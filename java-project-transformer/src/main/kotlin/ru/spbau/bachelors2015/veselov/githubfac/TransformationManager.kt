package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import java.nio.file.Paths
import java.util.Collections.shuffle

class TransformationManager(private val project: Project) {
    private var transformationDirectory: VirtualFile

    private val originalSubdirectory: VirtualFile

    private val transformedSubdirectory: VirtualFile

    private val cleaner = Cleaner(project)

    private val fileObtainer = FileObtainer(project)

    private val transformer = Transformer(project)

    init {
        val transformationName = "transformation"

        val existedDir = LocalFileSystem.getInstance().findFileByIoFile(
            Paths.get(project.baseDir.canonicalPath).resolve(transformationName).toFile()
        )

        ApplicationManager.getApplication().runWriteAction {
            existedDir?.delete(this)
        }

        transformationDirectory =
                TransformationManager.createRootChildFolder(
                    project,
                    project.baseDir,
                    transformationName
                )

        originalSubdirectory =
                TransformationManager.createRootChildFolder(
                    project,
                    transformationDirectory,
                    "original"
                )

        transformedSubdirectory =
                TransformationManager.createRootChildFolder(
                    project,
                    transformationDirectory,
                    "transformed"
                )
    }

    fun run() {
        val allFiles = fileObtainer.getAllJavaFiles()
        val files = allFiles.filter { isProbablyAppropriate(it) }
        cleaner.clean(files)

        val appropriateFiles = files.filter { isAppropriate(it) }
        Log.write("${appropriateFiles.size} good java files detected")
        appropriateFiles.forEach { Log.write(it.path) }

        val sampleFiles = getRandomSample(appropriateFiles)

        copyFilesToOriginal(sampleFiles)
        transformFiles(sampleFiles)
    }

    private fun getRandomSample(files: List<VirtualFile>) : List<VirtualFile> {
        val list = files.toMutableList()
        shuffle(list)

        // val result = list.take(1) // todo: 10?
        val result = list.filter { it.nameWithoutExtension == "EntityDamageEvent" }

        Log.write("${result.size} files in sample")
        for (file in result) {
            Log.write(file.path)
        }

        return result
    }

    private fun copyFilesToOriginal(files: List<VirtualFile>) {
        files.zip(1..files.size).forEach {
            (file, id) ->
            copyFileContentTo(originalSubdirectory, file, "$id.java", project, this)
        }
    }

    private fun transformFiles(files: List<VirtualFile>) {
        files.zip(1..files.size).forEach {
            (file, id) ->
            val psiFile = PsiManager.getInstance(project).findFile(file)
            if (psiFile == null || psiFile !is PsiJavaFile) {
                throw RuntimeException()
            }

            transformer.transformFile(psiFile)
            copyFileContentTo(transformedSubdirectory, file, "$id.java", project, this)
        }
    }

    private fun isProbablyAppropriate(file: VirtualFile) : Boolean {
        val document = FileDocumentManager.getInstance().getDocument(file)

        return document != null && linesOfCode(document) >= lowerBoundOnLOC
    }

    private fun isAppropriate(file: VirtualFile) : Boolean {
        val document = FileDocumentManager.getInstance().getDocument(file)

        return document != null // todo: && linesOfCode(document) in 400..800
    }

    private object TransformationManager {
        fun createRootChildFolder(
            project: Project,
            baseDir: VirtualFile,
            name: String
        ) : VirtualFile {
            var result: VirtualFile? = null

            ApplicationManager.getApplication().runWriteAction {
                result = baseDir.createChildDirectory(this, name)
            }

            return result!!
        }
    }
}
