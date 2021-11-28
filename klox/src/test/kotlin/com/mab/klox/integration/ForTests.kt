package com.mab.klox.integration

import com.mab.klox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ForTests : ShouldSpec({
    should("handle single expression body") {
        val source = """
            // Single-expression body.
            for (var c = 0; c < 3;) print c = c + 1;
        """.trimIndent()
        runScript(source) shouldBe """
            1
            2
            3
            
        """.trimIndent()
    }

    should("handle block body syntax") {
        val source = """
            for (var a = 0; a < 3; a = a + 1) {
              print a;
            }
        """.trimIndent()
        runScript(source) shouldBe """
            0
            1
            2
            
        """.trimIndent()
    }

    should("handle no variable") {
        val source = """
            var i = 0;
            for (; i < 2; i = i + 1) print i;
        """.trimIndent()
        runScript(source) shouldBe """
            0
            1
            
        """.trimIndent()
    }
})
