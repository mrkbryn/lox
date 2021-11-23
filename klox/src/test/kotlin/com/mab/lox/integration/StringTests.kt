package com.mab.lox.integration

import com.mab.lox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class StringTests : ShouldSpec({
    should("parse and print strings") {
        val source = """
            print "foobar";
        """.trimIndent()
        runScript(source) shouldBe """
            foobar
            
        """.trimIndent()
    }

    should("concatenate strings") {
        val source = """
            print "foo" + "bar" + "bar";
        """.trimIndent()
        runScript(source) shouldBe """
            foobarbar
            
        """.trimIndent()
    }

    should("error on unterminated strings") {
        val source = """
            print "this string is never closed!
        """.trimIndent()
        runScript(source) shouldBe """
            [line 1] Error: Unterminated string.
            [line 1] Error at end: Expect expression.
            
        """.trimIndent()
    }

    should("parse multiline strings") {
        val source = """
            var a = "1
            2
            3";
            print a;
        """.trimIndent()
        runScript(source) shouldBe """
            1
            2
            3
            
        """.trimIndent()
    }

    should("handle unicode strings") {
        val source = """
            print "A~¶Þॐஃ"; // expect: A~¶Þॐஃ
        """.trimIndent()
        runScript(source) shouldBe """
            A~¶Þॐஃ
            
        """.trimIndent()
    }
})
