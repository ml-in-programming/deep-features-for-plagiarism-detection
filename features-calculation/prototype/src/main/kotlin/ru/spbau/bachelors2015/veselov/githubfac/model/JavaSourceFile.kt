package ru.spbau.bachelors2015.veselov.githubfac.model

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import org.apache.commons.io.IOUtils
import java.nio.charset.Charset
import java.nio.file.Path


class JavaSourceFile(
    private val code: String,
    val fileDescription: String
) {
    constructor(
        pathToFile: Path
    ) : this(
        IOUtils.toString(pathToFile.toFile().inputStream(), Charset.defaultCharset()),
        pathToFile.toAbsolutePath().toString()
    )

    fun splitOnMethods() : List<JavaCodeSnippet> {
        val compilationUnit = JavaParser.parse(code)
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
                fileDescription + " " + it.nameAsString
            )
        }
    }
}
