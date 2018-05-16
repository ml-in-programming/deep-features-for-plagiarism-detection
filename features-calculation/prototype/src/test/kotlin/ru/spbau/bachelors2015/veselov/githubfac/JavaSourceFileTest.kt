package ru.spbau.bachelors2015.veselov.githubfac

import org.apache.commons.io.FileUtils.writeStringToFile
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import ru.spbau.bachelors2015.veselov.githubfac.model.JavaSourceFile
import java.nio.charset.Charset

class JavaSourceFileTest {
    @Rule
    @JvmField
    var folder = TemporaryFolder()

    @Test
    fun testSplitOnMethods() {
        val code =
            """
            package com.example;

            public class A {
                /**
                 * a javadoc of this method
                 */
                public int method() { return hidden(42); }

                private int hidden(int arg) {
                    while (arg > 0) {
                        arg--;
                    }

                    return arg;
                }

                @Override
                protected int noIndent() {
                return 42;
                }
            }
            """.trimIndent()

        val method1 =
            """
            public int method() { return hidden(42); }
            """.trimIndent()

        val method2 =
            """
            private int hidden(int arg) {
                    while (arg > 0) {
                        arg--;
                    }

                    return arg;
                }
            """.trimIndent()

        val method3 =
            """
            @Override
                protected int noIndent() {
                return 42;
                }
            """.trimIndent()

        val fileName = "source.java"
        val file = folder.newFile(fileName)
        writeStringToFile(file, code, Charset.defaultCharset())

        val methods = JavaSourceFile(file.toPath()).splitOnMethods()
        assertThat(
            methods.map { it.code },
            containsInAnyOrder(method1, method2, method3)
        )
    }
}