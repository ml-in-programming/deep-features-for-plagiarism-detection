package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.ast.body.Parameter
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserParameterDeclaration
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference

class JavaParameter(private val context: SourceContext,
                    private val parameter: Parameter) {
    fun simpleName(): String {
        return parameter.getNameAsString()
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

                    val parameterDeclaration = try {
                        symbolReference.correspondingDeclaration as JavaParserParameterDeclaration
                    } catch (e: ClassCastException) {
                        return
                    }

                    if (parameterDeclaration.wrappedNode === parameter) {
                        n.setName(newName)
                    }
                }
            }, null)

        parameter.setName(newName)
    }
}