package com.mab.klox.integration

import com.mab.klox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class FieldTests : ShouldSpec({
    should("call function field") {
        val source = """
            class Foo {}
            
            fun bar(a, b) {
                print "bar";
                print a;
                print b;
            }
            
            var foo = Foo();
            foo.bar = bar;
            
            foo.bar(1, 2);
        """.trimIndent()
        runScript(source) shouldBe """
            bar
            1
            2
            
        """.trimIndent()
    }
})
