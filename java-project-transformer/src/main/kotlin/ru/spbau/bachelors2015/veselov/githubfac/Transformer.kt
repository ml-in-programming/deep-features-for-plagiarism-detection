package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.psi.*
import com.intellij.refactoring.RefactoringFactory
import ru.spbau.bachelors2015.veselov.githubfac.identifiers.IdentifiersProducer

class Transformer(private val project: Project) {
    private val identifierProducer = IdentifiersProducer(project)

    private val factory = JavaPsiFacade.getInstance(project).elementFactory

    fun transformFile(file: PsiJavaFile) {
        Log.write(file.toString())

        shuffleClasses(file)
        renameIdentifiers(file)

        file.importList?.let { shuffleImports(it) }
    }

    fun shuffleImports(importList: PsiImportList) {
        val imports = mutableListOf<PsiImportStatementBase>()

        WriteCommandAction.runWriteCommandAction(project) {
            for (importStatement in importList.allImportStatements) {
                val element = importStatement.resolve()
                if (importStatement is PsiImportStaticStatement) {
                    imports.add(
                        factory.createImportStaticStatement(
                            importStatement.resolveTargetClass()!!,
                            (element as PsiNamedElement).name!!
                        )
                    )
                } else {
                    when (element) {
                        is PsiPackage -> imports.add(factory.createImportStatementOnDemand(element.qualifiedName))
                        is PsiClass -> imports.add(factory.createImportStatement(element))
                        else -> throw RuntimeException("Unable to resolve import")
                    }
                }

                importStatement.delete()
            }
        }

        imports.shuffle()
        WriteCommandAction.runWriteCommandAction(project) {
            for (i in imports.indices) {
                if (i == 0) {
                    importList.add(imports[i])
                } else {
                    importList.addAfter(imports[i], imports[i - 1])
                }
            }
        }
    }

    fun shuffleClass(clazz: PsiClass) {
        val anchor = clazz.lBrace
        val children = (
            clazz.methods.toList() as List<PsiElement> +
            clazz.fields +
            clazz.innerClasses
        ).toMutableList()

        fun copyPsiElement(element: PsiElement) : PsiElement {
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

        WriteCommandAction.runWriteCommandAction(project) {
            children.forEach { it.delete() }
        }

        newChildren.shuffle()
        WriteCommandAction.runWriteCommandAction(project) {
            newChildren.forEach {
                clazz.addAfter(it, anchor)
            }
        }
    }

    private fun shuffleClasses(file: PsiJavaFile) {
        object : JavaRecursiveElementVisitor() {
            override fun visitClass(aClass: PsiClass) {
                super.visitClass(aClass)
                shuffleClass(aClass)
            }
        }.visitElement(file)
    }

    private fun renameIdentifiers(file: PsiJavaFile) {
        val namedElements = mutableSetOf<PsiNamedElement>()

        object : JavaRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement?) {
                super.visitElement(element)

                if (element is PsiNamedElement) {
                    namedElements.add(element)
                    return
                }

                if (element is PsiJavaCodeReferenceElement) {
                    val namedElement =
                        DumbService.getInstance(project)
                                   .runReadActionInSmartMode(
                                       Computable {
                                           element.advancedResolve(false).element
                                       }
                                   )

                    if (namedElement != null) {
                        if (namedElement !is PsiPackage && namedElement.isWritable) {
                            namedElements.add(namedElement as PsiNamedElement)
                        }
                    } else {
                        throw RuntimeException("Failed to resolve reference to ${element.qualifiedName}")
                    }
                }
            }
        }.visitElement(file)

        val elementsToRename = namedElements.filter {
            if (it === file) {
                return@filter false
            }

            if (it is PsiMethod && it.isConstructor) {
                return@filter false
            }

            return@filter true
        }

        elementsToRename.zip(identifierProducer.produce(elementsToRename.size)).forEach {
            (element, newIdentifier) ->
            val oldName = element.name

            val newName = if (oldName != null) {
                newIdentifier.toSameNotationAs(oldName)
            } else {
                newIdentifier.toBigCamelNotation()
            }


            val refactoring =
                    RefactoringFactory.getInstance(project)
                                      .createRename(element, newName)

            WriteCommandAction.runWriteCommandAction(project) {
                refactoring.doRefactoring(refactoring.findUsages())
            }
        }
    }
}