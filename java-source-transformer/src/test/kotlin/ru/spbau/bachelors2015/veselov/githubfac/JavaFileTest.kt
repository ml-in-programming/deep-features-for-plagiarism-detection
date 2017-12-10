package ru.spbau.bachelors2015.veselov.githubfac

import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.BodyDeclaration
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.util.*

class JavaFileTest {
    @Test
    fun testGetDeclarations() {
        for (name in ResourcesManager.getAllNames()) {
            val actualDeclarations = ResourcesManager.getDeclarations(name) ?: continue

            println("testGetDeclarations: $name")
            testGetDeclarations(
                JavaFile(ResourcesManager.getOrigin(name)),
                actualDeclarations
            )
        }
    }

    private fun testGetDeclarations(file: JavaFile, actualDeclarations: List<String>) {
        assertThat(file.declarations().map { it.nameAsString }.sorted(),
                equalTo(actualDeclarations))
    }

    @Test
    fun testRenaming() {
        for (name in ResourcesManager.getAllNames()) {
            if (name == "Fields") {
                continue // todo: remove this skipping
            }

            val actualRenaming = ResourcesManager.getRenamed(name) ?: continue

            println("testRenaming: $name")
            testRenaming(
                JavaFile(ResourcesManager.getOrigin(name)),
                actualRenaming
            )
        }
    }

    private fun testRenaming(file: JavaFile, actualRenaming: String) {
        var counter = 1
        for (declaration in file.declarations()) {
            file.rename(declaration, "i$counter")
            counter++
        }

        assertThat(file.printCode(), equalTo(actualRenaming))
    }

    // todo: very similar methods: this one and other test cases
    @Test
    fun testMembersShift() {
        for (name in ResourcesManager.getAllNames()) {
            val actualShift = ResourcesManager.getShifted(name) ?: continue

            println("testMembersShift: $name")
            testMembersShift(
                JavaFile(ResourcesManager.getOrigin(name)),
                actualShift
            )
        }
    }

    private fun testMembersShift(file: JavaFile, actualShift: String) {
        val declarations = file.declarationsWithMembers()
        for (declaration in declarations) {
            val members: NodeList<BodyDeclaration<*>> = declaration.members
            Collections.rotate(members, 1)
            declaration.members = members
        }

        assertThat(file.printCode(), equalTo(actualShift))
    }
}