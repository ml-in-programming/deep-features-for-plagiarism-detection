package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
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
        val files = getFiles()
        removeComments(files)

        val appropriateFiles = getAppropriateFiles(files)
        val sampleFiles = getRandomSample(appropriateFiles)

        for (file in sampleFiles) {
            val copy = originalSubdirectory.createChildData(this, file.name)
            val document = FileDocumentManager.getInstance().getDocument(copy)
            document!!.setText(PsiManager.getInstance(project).findFile(file)!!.text)
        }

        for (file in sampleFiles) {
            val psiFile = PsiManager.getInstance(project).findFile(file)
            if (psiFile == null || !(psiFile is PsiJavaFile)) {
                throw RuntimeException()
            }

            transformFile(psiFile)
        }
    }

    private fun getFiles() : List<VirtualFile> {
        val result: MutableList<VirtualFile> = mutableListOf()

        ProjectFileIndex.SERVICE.getInstance(project).iterateContent {
            if (FileChecker.isProbablyAppropriate(it)) {
                result.add(it)
            }

            true
        }

        return result
    }

    private fun removeComments(files: List<VirtualFile>) {
        files.forEach {
            val psiFile = PsiManager.getInstance(project).findFile(it)
            if (psiFile == null || !(psiFile is PsiJavaFile)) {
                return@forEach
            }

            val comments = mutableListOf<PsiComment>()
            object : JavaRecursiveElementVisitor() {
                override fun visitComment(comment: PsiComment?) {
                    super.visitComment(comment)

                    if (comment != null) {
                        comments.add(comment)
                    }
                }
            }.visitElement(psiFile)

            comments.forEach { it.delete() }
        }
    }

    private fun getAppropriateFiles(files: List<VirtualFile>) : List<VirtualFile> {
        val result: MutableList<VirtualFile> = mutableListOf()

        files.forEach {
            if (FileChecker.isAppropriate(it)) {
                result.add(it)
            }
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

        // val result = list.take(1) // todo: 10?
        val result = list.filter { it.nameWithoutExtension == "EntityDamageEvent" }

        log.write("${result.size} files in sample")
        for (file in result) {
            log.write(file.path)
        }

        return result
    }

    private fun transformFile(file: PsiJavaFile) {
        log.write(file.toString())

        object : JavaRecursiveElementVisitor() {
            override fun visitClass(aClass: PsiClass) {
                super.visitClass(aClass)

                log.write(aClass.toString())
                transformClass(aClass)
            }
        }.visitElement(file)
    }

    private fun transformClass(clazz: PsiClass) {
        val anchor = clazz.lBrace
        val children = (
            clazz.methods.toList() as List<PsiElement> +
            clazz.fields +
            clazz.innerClasses
        ).toMutableList()

        fun copyPsiElement(element: PsiElement) : PsiElement {
            val factory = JavaPsiFacade.getInstance(project).elementFactory

            return when (element) {
                is PsiMethod -> factory.createMethodFromText(element.text, element.context)
                is PsiEnumConstant -> factory.createEnumConstantFromText(element.text, element.context)
                is PsiField -> factory.createFieldFromText(element.text, element.context)
                is PsiClass -> factory.createClassFromText(element.text, element.context).innerClasses.first()
                else -> throw RuntimeException()
            }
        }

        val newChildren: MutableList<PsiElement> =
                children.map { copyPsiElement(it) }.toMutableList()

        children.forEach { it.delete() }

        shuffle(newChildren)
        newChildren.forEach {
            clazz.addAfter(it, anchor)
        }
    }
}
