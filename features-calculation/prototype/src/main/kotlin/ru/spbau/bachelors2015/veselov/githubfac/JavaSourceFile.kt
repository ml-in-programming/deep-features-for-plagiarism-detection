package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import java.nio.file.Path


class JavaSourceFile(
    private val pathToFile: Path,
    private val typeSolver: JavaParserTypeSolver
) {
    val description = pathToFile.toAbsolutePath().toString()

    fun splitOnMethods() : List<JavaCodeSnippet> {
        val compilationUnit = JavaParser.parse(pathToFile)
        LexicalPreservingPrinter.setup(compilationUnit)

        val methods = mutableListOf<MethodDeclaration>()
        compilationUnit.accept(
            object : VoidVisitorAdapter<Void>() {
                override fun visit(node: MethodDeclaration, arg: Void?) {
                    super.visit(node, arg)
                    methods.add(node)
                }
            },
            null
        )

        return methods.map {
            JavaCodeSnippet(
                LexicalPreservingPrinter.print(it),
                description + " " +
                    JavaParserMethodDeclaration(it, typeSolver).qualifiedName
            )
        }
    }
}
