package ru.spbau.bachelors2015.veselov.githubfac

import org.apache.commons.io.FileUtils
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.nio.charset.Charset
import java.nio.file.Paths

class JavaFileTest {
    @Test
    fun testGetFields() {
        val fields: List<JavaField> = JavaFile(fileToString("/Fields.java")).fields()
        assertThat(fields.map { it.simpleName() }.sorted(),
                   equalTo(mutableListOf("a", "b", "b") as List<String>))
    }

    @Test
    fun testFieldsRenaming() {
        val file = JavaFile(fileToString("/Fields.java"))

        var counter = 1
        for (field in file.fields()) {
            field.renameTo("f$counter")
            counter++
        }

        assertThat(file.printCode(), equalTo(fileToString("/FieldsRenamed.java")))
    }

    private fun fileToString(fileName: String): String {
        val pathToFile = Paths.get(javaClass.getResource(fileName).file)
        return FileUtils.readFileToString(pathToFile.toFile(), null as Charset?)
    }
}