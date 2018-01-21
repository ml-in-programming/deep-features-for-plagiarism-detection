package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import java.util.*

class Transformer(private val project: Project) {
    fun transformFile(file: PsiJavaFile) {
        Log.write(file.toString())

        object : JavaRecursiveElementVisitor() {
            override fun visitClass(aClass: PsiClass) {
                super.visitClass(aClass)

                Log.write(aClass.toString())
                transformClass(aClass)
            }
        }.visitElement(file)
    }

    fun transformClass(clazz: PsiClass) {
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

        Collections.shuffle(newChildren)
        newChildren.forEach {
            clazz.addAfter(it, anchor)
        }
    }
}