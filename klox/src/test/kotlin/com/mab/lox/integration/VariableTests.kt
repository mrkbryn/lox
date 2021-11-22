package com.mab.lox.integration

import com.mab.lox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class VariableTests : ShouldSpec({
    should("catch unassigned variable") {
        val source = """
            print x;
        """.trimIndent()
        runScript(source) shouldBe """
            Undefined variable 'x'. 
            [line 1]
            
        """.trimIndent()
    }

    should("assign variables") {
        val source = """
            var x = 10;
            print x;
        """.trimIndent()
        runScript(source) shouldBe """
            10
            
        """.trimIndent()
    }

    should("reassign variables") {
        val source = """
            var x = 10;
            x = 20;
            print x;
        """.trimIndent()
        runScript(source) shouldBe """
            20
            
        """.trimIndent()
    }
})
