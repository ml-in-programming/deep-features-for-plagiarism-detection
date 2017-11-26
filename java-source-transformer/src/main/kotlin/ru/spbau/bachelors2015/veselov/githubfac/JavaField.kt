package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserFieldDeclaration
import com.github.javaparser.symbolsolver.model.declarations.ValueDeclaration
import com.github.javaparser.symbolsolver.model.resolution.UnsolvedSymbolException
import java.lang.reflect.Field

class JavaField(private val context: SourceContext,
                private val variableDeclarator: VariableDeclarator) {
    fun simpleName(): String {
        return variableDeclarator.getNameAsString()
    }

    fun renameTo(newName: String) {
        fun isThisField(accessExpression: NodeWithSimpleName<out Node>) : Boolean {
            try {
                val symbolReference =
                        JavaParserFacade.get(context.javaParserTypeSolver)
                                        .solve(accessExpression.name)

                if (!symbolReference.isSolved) {
                    return false
                }

                val declaration: ValueDeclaration = symbolReference.correspondingDeclaration
                if (!declaration.isField) {
                    return false
                }

                val fieldDeclaration = symbolReference
                        .correspondingDeclaration.asField() as JavaParserFieldDeclaration

                val field: Field = fieldDeclaration.javaClass
                        .getDeclaredField("variableDeclarator")

                field.setAccessible(true)

                val thatVariableDeclarator = field.get(fieldDeclaration) as VariableDeclarator

                return thatVariableDeclarator === variableDeclarator
            } catch (e: UnsolvedSymbolException) {
                return false
            }
        }

        context.allByNameExpressions().filter { isThisField(it) }.forEach {
            it.setName(newName)
        }

        variableDeclarator.setName(newName)
    }
}
