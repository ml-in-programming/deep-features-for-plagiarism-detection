package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.refactoring.RefactoringFactory

class Transformer(private val project: Project) {
    fun transformFile(file: PsiJavaFile) {
        Log.write(file.toString())

        // shuffleInnerClasses(file) todo
        renameIdentifiers(file)
    }

    fun shuffleClass(clazz: PsiClass) {
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

        newChildren.shuffle()
        newChildren.forEach {
            clazz.addAfter(it, anchor)
        }
    }

    private fun shuffleInnerClasses(file: PsiJavaFile) {
        object : JavaRecursiveElementVisitor() {
            override fun visitClass(aClass: PsiClass) {
                super.visitClass(aClass)

                Log.write(aClass.toString())
                shuffleClass(aClass)
            }
        }.visitElement(file)
    }

    private fun renameIdentifiers(file: PsiJavaFile) {
        val namedElements = mutableSetOf<PsiNamedElement>()

        object : JavaRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement?) {
                super.visitElement(element)

                if (element === file) {
                    return
                }

                if (element is PsiNamedElement) {
                    namedElements.add(element)
                    return
                }

                if (element is PsiJavaCodeReferenceElement) {
                    val namedElement = element.advancedResolve(false).element
                    if (namedElement != null) {
                        Log.write("$namedElement is referenced")

                        if (namedElement !is PsiPackage && namedElement.isWritable) {
                            namedElements.add(namedElement as PsiNamedElement)
                        }
                    }
                }
            }
        }.visitElement(file)

        var ctr = 0
        namedElements.forEach {
            val refactoring =
                    RefactoringFactory.getInstance(project)
                            .createRename(it, "name${ctr++}")

            refactoring.doRefactoring(refactoring.findUsages())
        }
    }
}