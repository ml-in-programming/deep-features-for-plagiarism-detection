package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.printer.SourcePrinter
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter
import java.nio.file.Path

class JavaSourceFile(private val pathToFile: Path) {
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
            JavaCodeSnippet(LexicalPreservingPrinter.print(it))
        }
    }
}
