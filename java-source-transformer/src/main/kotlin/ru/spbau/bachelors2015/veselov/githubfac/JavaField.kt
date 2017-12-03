package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.FieldAccessExpr
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference
import java.lang.reflect.Field

class JavaField(private val context: SourceContext,
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

                    val fieldDeclaration = try {
                        symbolReference.correspondingDeclaration as JavaParserFieldDeclaration
                    } catch (e: ClassCastException) {
                        return
                    }

                    val field: Field = fieldDeclaration.javaClass
                            .getDeclaredField("variableDeclarator")

                    field.setAccessible(true)
                    val thatVariableDeclarator =
                            field.get(fieldDeclaration) as VariableDeclarator

                    if (thatVariableDeclarator === variableDeclarator) {
                        n.setName(newName)
                    }
                }

                override fun visit(
                    n: FieldAccessExpr,
                    void: Void?
                ) {
                }
            }, null)

        variableDeclarator.setName(newName)
    }
}
