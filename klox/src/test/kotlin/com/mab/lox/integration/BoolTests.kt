package com.mab.lox.integration

import com.mab.lox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class BoolTests : ShouldSpec({
    should("check equality") {
        val source = """
            print true == true;    // expect: true
            print true == false;   // expect: false
            print false == true;   // expect: false
            print false == false;  // expect: true

            // Not equal to other types.
            print true == 1;        // expect: false
            print false == 0;       // expect: false
            print true == "true";   // expect: false
            print false == "false"; // expect: false
            print false == "";      // expect: false

            print true != true;    // expect: false
            print true != false;   // expect: true
            print false != true;   // expect: true
            print false != false;  // expect: false

            // Not equal to other types.
            print true != 1;        // expect: true
            print false != 0;       // expect: true
            print true != "true";   // expect: true
            print false != "false"; // expect: true
            print false != "";      // expect: true
        """.trimIndent()
        runScript(source) shouldBe """
            true
            false
            false
            true
            false
            false
            false
            false
            false
            false
            true
            true
            false
            true
            true
            true
            true
            true
            
        """.trimIndent()
    }

    should("check negation") {
        val source = """
            print !true;    // expect: false
            print !false;   // expect: true
            print !!true;   // expect: true
        """.trimIndent()
        runScript(source) shouldBe """
            false
            true
            true
            
        """.trimIndent()
    }
})
