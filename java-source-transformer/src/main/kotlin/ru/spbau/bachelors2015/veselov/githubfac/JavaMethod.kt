package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.MethodCallExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference

class JavaMethod(private val context: SourceContext,
                 private val methodDeclaration: MethodDeclaration) {
    fun simpleName(): String {
        return methodDeclaration.getNameAsString()
    }

    fun renameTo(newName: String) {
        context.unit.accept(
            object : VoidVisitorAdapter<Void?>() {
                override fun visit(
                    n: MethodCallExpr,
                    void: Void?
                ) {
                    val symbolReference: SymbolReference<ResolvedMethodDeclaration> =
                            JavaParserFacade.get(context.javaParserTypeSolver)
                                           .solve(n)

                    if (!symbolReference.isSolved) {
                        return
                    }

                    val methodDeclaration = try {
                        symbolReference.correspondingDeclaration as JavaParserMethodDeclaration
                    } catch (e: ClassCastException) {
                        return
                    }

                    if (methodDeclaration.wrappedNode === this@JavaMethod.methodDeclaration) {
                        n.setName(newName)
                    }
                }
            },
            null)

        methodDeclaration.setName(newName)
    }
}