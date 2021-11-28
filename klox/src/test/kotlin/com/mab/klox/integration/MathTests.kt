package com.mab.klox.integration

import com.mab.klox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class MathTests : ShouldSpec({
    should("parse and print numeric values") {
        runScript("print 1;").trim() shouldBe "1"
        runScript("print 1.0;").trim() shouldBe "1"
    }

    should("handle math functions") {
        runScript("print 1 + 5;").trim() shouldBe "6"
        runScript("print 100 - 25;").trim() shouldBe "75"
        runScript("print 10 * 25;").trim() shouldBe "250"
        runScript("print 100 / 10;").trim() shouldBe "10"
        runScript("print 2.5 * 4;").trim() shouldBe "10"
        runScript("print 2.2 * 2;").trim() shouldBe "4.4"
    }

    should("validate number operands") {
        val source = """
            print 5 + "hello";
        """.trimIndent()
        runScript(source) shouldBe """
            Operands must be two numbers or two strings. 
            [line 1]
            
        """.trimIndent()
    }
})
