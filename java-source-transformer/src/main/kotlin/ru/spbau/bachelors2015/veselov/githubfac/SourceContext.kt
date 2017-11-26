package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter
import com.github.javaparser.printer.XmlPrinter
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import org.apache.commons.io.FileUtils
import java.nio.charset.Charset

class SourceContext(sourceCode: String) {
    val javaParserTypeSolver: TypeSolver

    val unit: CompilationUnit

    init {
        val tmpDir = createTempDir()
        val tmpFile = createTempFile(directory = tmpDir)
        FileUtils.write(tmpFile, sourceCode, null as Charset?)

        javaParserTypeSolver = CombinedTypeSolver()
        javaParserTypeSolver.add(ReflectionTypeSolver())
        javaParserTypeSolver.add(JavaParserTypeSolver(tmpDir))

        unit = JavaParser.parse(tmpFile)
        LexicalPreservingPrinter.setup(unit) // pitfall
    }

    fun fields(): List<JavaField> {
        return unit.accept(
            object : GenericListVisitorAdapter<JavaField, Void?>() {
                override fun visit(
                        n: FieldDeclaration,
                        void: Void?
                ): List<JavaField> {
                    val list: MutableList<JavaField> =
                            super.visit(n, void) ?: mutableListOf()

                    n.variables.mapTo(list) { JavaField(this@SourceContext, it) }

                    return list
                }
            }, null)
    }

    fun allByNameExpressions() : List<NodeWithSimpleName<out Node>> {
        return unit.accept(
            object : GenericListVisitorAdapter<NodeWithSimpleName<out Node>, Void?>() {
                override fun visit(
                    n: NameExpr,
                    void: Void?
                ): List<NodeWithSimpleName<out Node>> {
                    val list: MutableList<NodeWithSimpleName<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    list.add(n)
                    return list
                }

                override fun visit(
                    n: FieldAccessExpr,
                    void: Void?
                ): List<NodeWithSimpleName<out Node>> {
                    val list: MutableList<NodeWithSimpleName<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    list.add(n)
                    return list
                }
            }, null)
    }
}
