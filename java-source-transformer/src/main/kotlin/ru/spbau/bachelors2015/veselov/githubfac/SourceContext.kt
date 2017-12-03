package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter
import com.github.javaparser.printer.JsonPrinter
import com.github.javaparser.printer.XmlPrinter
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver
import com.github.javaparser.symbolsolver.resolution.SymbolSolver
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

        val pc = ParserConfiguration()
        pc.setSymbolResolver(JavaSymbolSolver(javaParserTypeSolver))

        JavaParser.setStaticConfiguration(pc)
        unit = JavaParser.parse(tmpFile)
        val printer = JsonPrinter(true)
        println(printer.output(unit))


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

    fun localVars(): List<JavaLocalVar> {
        return unit.accept(
            object : GenericListVisitorAdapter<JavaLocalVar, Void?>() {
                override fun visit(
                    n: VariableDeclarationExpr,
                    void: Void?
                ): List<JavaLocalVar> {
                    val list: MutableList<JavaLocalVar> =
                            super.visit(n, void) ?: mutableListOf()

                    n.variables.mapTo(list) { JavaLocalVar(this@SourceContext, it) }

                    return list
                }
            }, null)
    }

    fun methods(): List<JavaMethod> {
        return unit.accept(
            object : GenericListVisitorAdapter<JavaMethod, Void?>() {
                override fun visit(
                    n: MethodDeclaration,
                    void: Void?
                ): List<JavaMethod> {
                    val list: MutableList<JavaMethod> =
                            super.visit(n, void) ?: mutableListOf()

                    list.add(JavaMethod(this@SourceContext, n))

                    return list
                }
            }, null)
    }

    fun parameters(): List<JavaParameter> {
        return unit.accept(
            object : GenericListVisitorAdapter<JavaParameter, Void?>() {
                override fun visit(
                    n: Parameter,
                    void: Void?
                ): List<JavaParameter> {
                    val list: MutableList<JavaParameter> =
                            super.visit(n, void) ?: mutableListOf()

                    list.add(JavaParameter(this@SourceContext, n))

                    return list
                }
            }, null)
    }
}
