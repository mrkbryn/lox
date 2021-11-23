package com.mab.lox.integration

import com.mab.lox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class CommentsTest : ShouldSpec({
    should("handle comment at EOF") {
        val source = """
            print "ok";
            // comment
        """.trimIndent()
        runScript(source) shouldBe """
            ok
            
        """.trimIndent()
    }

    should("handle file with only comment") {
        val source = """
            // comment
        """.trimIndent()
        runScript(source) shouldBe ""
    }

    should("handle unicode comments") {
        val source = """
            // Unicode characters are allowed in comments.
            //
            // Latin 1 Supplement: £§¶ÜÞ
            // Latin Extended-A: ĐĦŋœ
            // Latin Extended-B: ƂƢƩǁ
            // Other stuff: ឃᢆ᯽₪ℜ↩⊗┺░
            // Emoji: ☃☺♣

            print "ok";
        """.trimIndent()
        runScript(source) shouldBe """
            ok
            
        """.trimIndent()
    }
})
