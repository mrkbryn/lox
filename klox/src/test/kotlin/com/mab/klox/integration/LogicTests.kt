package com.mab.klox.integration

import com.mab.klox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class LogicTests : ShouldSpec({
    should("handle truth tests") {
        val source = """
            if (false) print "bad"; else print "false";
            if (nil) print "bad"; else print "nil";
            if (true) print true;
            if (0) print 0;
            if ("") print "empty";
        """.trimIndent()
        runScript(source) shouldBe """
            false
            nil
            true
            0
            empty
            
        """.trimIndent()
    }
})
