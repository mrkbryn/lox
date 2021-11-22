package com.mab.lox.integration

import com.mab.lox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class WhileTests : ShouldSpec({
    should("execute standard while") {
        val source = """
            var c = 0;
            while (c < 3) print c = c + 1;
            
            var a = 0;
            while (a < 3) {
                print a;
                a = a + 1;
            }
            
            while (false) if (true) 1; else 2;
            while (false) while (true) 1;
            while (false) for (;;) 1;
        """.trimIndent()
        runScript(source) shouldBe """
            1
            2
            3
            0
            1
            2
            
        """.trimIndent()
    }
})
