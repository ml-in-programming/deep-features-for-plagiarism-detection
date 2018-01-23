package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import java.nio.file.Paths
import java.util.Collections.shuffle

class TransformationManager(private val project: Project) {
    private var transformationDirectory: VirtualFile

    private var originalSubdirectory: VirtualFile

    private var transformedSubdirectory: VirtualFile

    private val cleaner = Cleaner(project)

    private val fileObtainer = FileObtainer(project)

    private val transformer = Transformer(project)

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

    fun run() {
        val files = fileObtainer.getAllJavaFiles().filter { isProbablyAppropriate(it) }
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
        for (file in files) {
            copyFileTo(originalSubdirectory, file, project, this)
        }
    }

    private fun transformFiles(files: List<VirtualFile>) {
        for (file in files) {
            val psiFile = PsiManager.getInstance(project).findFile(file)
            if (psiFile == null || !(psiFile is PsiJavaFile)) {
                throw RuntimeException()
            }

            transformer.transformFile(psiFile)
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
}