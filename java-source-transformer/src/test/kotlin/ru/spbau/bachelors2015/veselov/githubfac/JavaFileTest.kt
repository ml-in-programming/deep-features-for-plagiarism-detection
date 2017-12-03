package ru.spbau.bachelors2015.veselov.githubfac

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class JavaFileTest {
    @Test
    fun testGetDeclarations() {
        for (name in ResourcesManager.getAllNames()) {
            println("testGetDeclarations: $name")
            testGetDeclarations(
                JavaFile(ResourcesManager.getOrigin(name)),
                ResourcesManager.getDeclarations(name)
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

            println("testRenaming: $name")
            testRenaming(
                JavaFile(ResourcesManager.getOrigin(name)),
                ResourcesManager.getRenamed(name)
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
}