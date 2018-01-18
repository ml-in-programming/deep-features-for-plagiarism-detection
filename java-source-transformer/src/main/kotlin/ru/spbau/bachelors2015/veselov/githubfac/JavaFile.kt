package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.nodeTypes.NodeWithMembers
import com.github.javaparser.ast.nodeTypes.NodeWithParameters
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName
import com.github.javaparser.ast.visitor.GenericListVisitorAdapter
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter
import com.github.javaparser.resolution.declarations.ResolvedDeclaration
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference
import java.lang.reflect.Field
import java.nio.file.Path
import java.util.*

class JavaFile(
    val path: Path,
    private val unit: CompilationUnit,
    private val javaParserFacade: JavaParserFacade
) {
    fun printCode() : String {
        return LexicalPreservingPrinter.print(unit)
    }

    fun setupPrinting() {
        LexicalPreservingPrinter.setup(unit) // todo: pitfall
    }

    fun linesOfCode() : Int {
        return 1 + unit.tokenRange.get().count { it.category.isEndOfLine }
    }

    fun usages(declaration: NodeWithSimpleName<out Node>) : List<NodeWithSimpleName<out Node>>{
        println("Usages of $declaration")

        return unit.accept(
            object : GenericListVisitorAdapter<NodeWithSimpleName<out Node>, Void?>() {
                override fun visit(
                    n: NameExpr,
                    void: Void?
                ) : List<NodeWithSimpleName<out Node>> {
                    val list: MutableList<NodeWithSimpleName<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    if (isUsage(javaParserFacade.solve(n))) {
                        list.add(n)
                    }

                    return list
                }

                override fun visit(
                    n: MethodCallExpr,
                    void: Void?
                ) : List<NodeWithSimpleName<out Node>> {
                    val list: MutableList<NodeWithSimpleName<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    if (isUsage(javaParserFacade.solve(n))) {
                        list.add(n)
                    }

                    return list
                }

                override fun visit(
                    n: FieldAccessExpr,
                    void: Void?
                ) : List<NodeWithSimpleName<out Node>> {
                    val list: MutableList<NodeWithSimpleName<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    // todo

                    return list
                }

                private fun isUsage(
                    reference: SymbolReference<out ResolvedDeclaration>
                ) : Boolean {
                    if (!reference.isSolved) {
                        return false
                    }

                    return getDeclarationNode(reference.correspondingDeclaration) === declaration
                }
            }, null)
    }

    fun rename(declaration: NodeWithSimpleName<out Node>, newName: String) {
        for (usage in usages(declaration)) {
            usage.setName(newName)
        }

        declaration.setName(newName)
    }

    fun declarations(): List<NodeWithSimpleName<out Node>> {
        return unit.accept(
            object : GenericListVisitorAdapter<NodeWithSimpleName<out Node>, Void?>() {
                // classes and interfaces
                override fun visit(
                    n: ClassOrInterfaceDeclaration,
                    void: Void?
                ): List<NodeWithSimpleName<out Node>> {
                    val list: MutableList<NodeWithSimpleName<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    list.add(n)

                    return list
                }

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

    fun declarationsWithMembers(): List<NodeWithMembers<out Node>> {
        return unit.accept(
            object : GenericListVisitorAdapter<NodeWithMembers<out Node>, Void?>() {
                // fields
                override fun visit(
                    n: ClassOrInterfaceDeclaration,
                    void: Void?
                ): List<NodeWithMembers<out Node>> {
                    val list: MutableList<NodeWithMembers<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    list.add(n)

                    return list
                }
            }, null)
    }

    fun declarationsWithParameters(): List<NodeWithParameters<out Node>> {
        return unit.accept(
            object : GenericListVisitorAdapter<NodeWithParameters<out Node>, Void?>() {
                override fun visit(
                    n: MethodDeclaration,
                    void: Void?
                ): List<NodeWithParameters<out Node>> {
                    val list: MutableList<NodeWithParameters<out Node>> =
                            super.visit(n, void) ?: mutableListOf()

                    list.add(n)

                    return list
                }
            }, null)
    }

    fun shuffleMembers(node: NodeWithMembers<out Node>, random: Random) {
        // todo: indentation

        val members: NodeList<BodyDeclaration<*>> = node.members
        Collections.shuffle(members, random)
        node.members = members
    }

    fun shuffleParameters(node: NodeWithParameters<out Node>, random: Random) {
        // todo: looks similar to previous function

        val parameters: NodeList<Parameter> = node.parameters
        Collections.shuffle(parameters, random)
        node.parameters = parameters
    }

    private fun getDeclarationNode(declaration: ResolvedDeclaration) : Node? {
        if (declaration is JavaParserFieldDeclaration) {
            val field: Field = declaration.javaClass.getDeclaredField("variableDeclarator")

            field.setAccessible(true)
            return field.get(declaration) as Node
        }

        val method = try {
            declaration.javaClass.getMethod("getWrappedNode")
        } catch (_: NoSuchMethodException) {
            return null
        }

        return method.invoke(declaration) as Node
    }
}
