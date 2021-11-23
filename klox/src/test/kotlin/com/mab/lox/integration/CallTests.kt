package com.mab.lox.integration

import com.mab.lox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class CallTests : ShouldSpec({
    should("not be allowed to call boolean values") {
        val source = """
            true();
        """.trimIndent()
        runScript(source) shouldBe """
            Can only call functions and classes. 
            [line 1]
            
        """.trimIndent()
    }

    should("not be allowed to call nil values") {
        val source = """
            nil();
        """.trimIndent()
        runScript(source) shouldBe """
            Can only call functions and classes. 
            [line 1]
            
        """.trimIndent()
    }

    should("not be allowed to call numeric values") {
        val source = """
            123();
        """.trimIndent()
        runScript(source) shouldBe """
            Can only call functions and classes. 
            [line 1]
            
        """.trimIndent()
    }

    should("not be allowed to call objects") {
        val source = """
            class Foo {}
            var foo = Foo();
            foo();
        """.trimIndent()
        runScript(source) shouldBe """
            Can only call functions and classes. 
            [line 1]
            
        """.trimIndent()
    }

    should("not be allowed to call string values") {
        val source = """
            "str"();
        """.trimIndent()
        runScript(source) shouldBe """
            Can only call functions and classes. 
            [line 1]
            
        """.trimIndent()
    }
})
