package ru.spbau.bachelors2015.veselov.githubfac.identifiers

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import ru.spbau.bachelors2015.veselov.githubfac.FileObtainer

class IdentifiersProducer(project: Project) {
    private val identifiers: List<Identifier> = IdentifiersProducer.getIdentifiers(project)

    fun produce(amount: Int) : List<Identifier> {
        if (amount > identifiers.size) {
            throw IllegalArgumentException()
        }

        return identifiers.shuffled().take(amount)
    }

    private object IdentifiersProducer {
        fun getIdentifiers(project: Project) : List<Identifier> {
            val fileObtainer = FileObtainer(project)

            val result = mutableSetOf<Identifier>()
            fileObtainer.getAllJavaFiles().forEach { result.addAll(getIdentifiers(project, it)) }

            return result.toList()
        }

        fun getIdentifiers(project: Project, file: VirtualFile) : Set<Identifier> {
            val psiFile = PsiManager.getInstance(project).findFile(file)
            if (psiFile == null || !(psiFile is PsiJavaFile)) {
                throw RuntimeException()
            }

            val result = mutableSetOf<Identifier>()
            object : JavaRecursiveElementVisitor() {
                override fun visitElement(element: PsiElement?) {
                    super.visitElement(element)

                    if (element !is PsiNamedElement || element is PsiFile) {
                        return
                    }

                    val name = element.name
                    if (name != null && name.isIdentifier()) {
                        result.add(Identifier(name))
                    }
                }
            }.visitElement(psiFile)

            return result
        }
    }
}
