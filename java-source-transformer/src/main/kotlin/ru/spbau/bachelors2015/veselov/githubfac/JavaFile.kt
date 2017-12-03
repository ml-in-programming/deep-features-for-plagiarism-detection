package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.printer.JsonPrinter
import com.github.javaparser.printer.XmlPrinter
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter
import com.github.javaparser.resolution.declarations.ResolvedDeclaration
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver
import com.github.javaparser.symbolsolver.resolution.SymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import org.apache.commons.io.FileUtils
import java.lang.reflect.Field
import java.nio.charset.Charset

class JavaFile(sourceCode: String) {
    private val javaParserTypeSolver: TypeSolver

    private val javaParserFacade: JavaParserFacade

    private val unit: CompilationUnit

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

        javaParserFacade = JavaParserFacade.get(javaParserTypeSolver)

        LexicalPreservingPrinter.setup(unit) // pitfall
    }

    fun printCode() : String {
        return LexicalPreservingPrinter.print(unit)
    }

    fun rename(node: NodeWithSimpleName<out Node>, newName: String) {
        unit.accept(
            object : VoidVisitorAdapter<Void?>() {
                override fun visit(
                    n: NameExpr,
                    void: Void?
                ) {
                    super.visit(n, void)

                    rename(n, javaParserFacade.solve(n))
                }

                override fun visit(
                    n: MethodCallExpr,
                    void: Void?
                ) {
                    super.visit(n, void)

                    rename(n, javaParserFacade.solve(n))
                }

                override fun visit(
                    n: FieldAccessExpr,
                    void: Void?
                ) {
                    super.visit(n, void)

                    // todo
                }

                private fun rename(
                    n: NodeWithSimpleName<out Node>,
                    reference: SymbolReference<out ResolvedDeclaration>
                ) {
                    if (!reference.isSolved) {
                        return
                    }

                    val declaration = reference.correspondingDeclaration
                    if (getDeclarationNode(declaration) === node) {
                        n.setName(newName)
                    }
                }
            }, null)

        node.setName(newName)
    }

    fun declarations(): List<NodeWithSimpleName<out Node>> {
        return unit.accept(
            object : GenericListVisitorAdapter<NodeWithSimpleName<out Node>, Void?>() {
                // fields
                override fun visit(
                    n: FieldDeclaration,
                    void: Void?
                ): List<NodeWithSimpleName<out Node>> {
                    val list: MutableList<NodeWithSimpleName<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    list.addAll(n.variables)

                    return list
                }

                // local variables
                override fun visit(
                    n: VariableDeclarationExpr,
                    void: Void?
                ): List<NodeWithSimpleName<out Node>> {
                    val list: MutableList<NodeWithSimpleName<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    list.addAll(n.variables)

                    return list
                }

                // methods
                override fun visit(
                    n: MethodDeclaration,
                    void: Void?
                ): List<NodeWithSimpleName<out Node>> {
                    val list: MutableList<NodeWithSimpleName<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    list.add(n)

                    return list
                }

                // parameters
                override fun visit(
                    n: Parameter,
                    void: Void?
                ): List<NodeWithSimpleName<out Node>> {
                    val list: MutableList<NodeWithSimpleName<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    list.add(n)

                    return list
                }
            }, null)
    }

    private fun getDeclarationNode(declaration: ResolvedDeclaration) : Node {
        if (declaration is JavaParserFieldDeclaration) {
            val field: Field = declaration.javaClass.getDeclaredField("variableDeclarator")

            field.setAccessible(true)
            return field.get(declaration) as Node
        }

        val method = declaration.javaClass.getMethod("getWrappedNode")
        return method.invoke(declaration) as Node
    }
}
