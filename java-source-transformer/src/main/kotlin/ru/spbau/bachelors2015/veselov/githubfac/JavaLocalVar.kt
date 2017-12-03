package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserSymbolDeclaration
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference

class JavaLocalVar(private val context: SourceContext,
                   private val variableDeclarator: VariableDeclarator) {
    fun simpleName(): String {
        return variableDeclarator.getNameAsString()
    }

    fun renameTo(newName: String) {
        context.unit.accept(
            object : VoidVisitorAdapter<Void?>() {
                override fun visit(
                    n: NameExpr,
                    void: Void?
                ) {
                    val symbolReference: SymbolReference<out ResolvedValueDeclaration> =
                            JavaParserFacade.get(context.javaParserTypeSolver)
                                            .solve(n)

                    if (!symbolReference.isSolved) {
                        return
                    }

                    val declaration = try {
                        symbolReference.correspondingDeclaration as JavaParserSymbolDeclaration
                    } catch (e: ClassCastException) {
                        return
                    }

                    if (declaration.wrappedNode === variableDeclarator) {
                        n.setName(newName)
                    }
                }
            }, null)

        variableDeclarator.setName(newName)
    }
}