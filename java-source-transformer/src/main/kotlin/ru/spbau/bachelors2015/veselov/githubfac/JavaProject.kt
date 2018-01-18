package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.JavaParser
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

class JavaProject(sourceDirPath: Path, pathsToJars: List<Path>? = null) {
    private val javaParserTypeSolver: TypeSolver

    private val javaParserFacade: JavaParserFacade

    val files: List<JavaFile>

    init {
        javaParserTypeSolver = CombinedTypeSolver()
        javaParserTypeSolver.add(ReflectionTypeSolver())

        pathsToJars?.forEach {
            javaParserTypeSolver.add(JarTypeSolver(it.toString()))
        }

        javaParserTypeSolver.add(JavaParserTypeSolver(sourceDirPath.toFile()))

        val pc = ParserConfiguration()
        pc.setSymbolResolver(JavaSymbolSolver(javaParserTypeSolver))
        JavaParser.setStaticConfiguration(pc)

        javaParserFacade = JavaParserFacade.get(javaParserTypeSolver)

        val unitsTmp = arrayListOf<JavaFile>()

        Files.walkFileTree(sourceDirPath,
            object : SimpleFileVisitor<Path>() {
                override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
                    unitsTmp.add(
                        JavaFile(
                            sourceDirPath.relativize(path),
                            JavaParser.parse(path.toFile()),
                            javaParserFacade
                        )
                    )

                    return FileVisitResult.CONTINUE
                }
            }
        )

        files = unitsTmp
    }

    fun declarations(): List<NodeWithSimpleName<out Node>> {
        val result = arrayListOf<NodeWithSimpleName<out Node>>()
        files.forEach { result.addAll(it.declarations()) }
        return result
    }
}
