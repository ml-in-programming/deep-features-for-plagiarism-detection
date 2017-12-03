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
    fun testGetLocals() {
        val locals: List<JavaLocalVar> = JavaFile(fileToString("/Locals.java")).localVars()
        assertThat(locals.map { it.simpleName() }.sorted(),
                equalTo(mutableListOf("i", "i", "i", "i") as List<String>))
    }

    @Test
    fun testGetMethods() {
        val methods: List<JavaMethod> = JavaFile(fileToString("/Methods.java")).methods()
        assertThat(methods.map { it.simpleName() }.sorted(),
                equalTo(mutableListOf("a", "b", "b") as List<String>))
    }

    @Test
    fun testGetParameters() {
        val parameters: List<JavaParameter> =
                JavaFile(fileToString("/Parameters.java")).parameters()
        assertThat(parameters.map { it.simpleName() }.sorted(),
                equalTo(mutableListOf("a", "a", "b", "c", "c") as List<String>))
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

    @Test
    fun testLocalsRenaming() {
        val file = JavaFile(fileToString("/Locals.java"))

        var counter = 1
        for (local in file.localVars()) {
            local.renameTo("l$counter")
            counter++
        }

        assertThat(file.printCode(), equalTo(fileToString("/LocalsRenamed.java")))
    }

    @Test
    fun testMethodsRenaming() {
        val file = JavaFile(fileToString("/Methods.java"))

        var counter = 1
        for (method in file.methods()) {
            method.renameTo("m$counter")
            counter++
        }

        assertThat(file.printCode(), equalTo(fileToString("/MethodsRenamed.java")))
    }

    @Test
    fun testParametersRenaming() {
        val file = JavaFile(fileToString("/Parameters.java"))

        var counter = 1
        for (parameter in file.parameters()) {
            parameter.renameTo("a$counter")
            counter++
        }

        assertThat(file.printCode(), equalTo(fileToString("/ParametersRenamed.java")))
    }

    private fun fileToString(fileName: String): String {
        val pathToFile = Paths.get(javaClass.getResource(fileName).file)
        return FileUtils.readFileToString(pathToFile.toFile(), null as Charset?)
    }
}