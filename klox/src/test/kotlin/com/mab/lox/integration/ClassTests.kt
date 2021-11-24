package com.mab.lox.integration

import com.mab.lox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ClassTests : ShouldSpec({
    should("handle empty class definitions") {
        val source = """
            class Foo {}
            print Foo;
        """.trimIndent()
        runScript(source) shouldBe """
            Foo
            
        """.trimIndent()
    }

    should("disallow inheriting from same class") {
        val source = """
            class Foo < Foo {}
        """.trimIndent()
        runScript(source) shouldBe """
            [line 1] Error at 'Foo': A class can't inherit from itself.
            
        """.trimIndent()
    }

    should("inherit methods") {
        val source = """
            class Foo {
                inFoo() {
                    print "in foo";
                }
            }
            
            class Bar < Foo {
                inBar() {
                    print "in bar";
                }
            }
            
            class Baz < Bar {
                inBaz() {
                    print "in baz";
                }
            }
            
            // baz should inherit all three methods.
            var baz = Baz();
            baz.inFoo();
            baz.inBar();
            baz.inBaz();
        """.trimIndent()
        runScript(source) shouldBe """
            in foo
            in bar
            in baz
            
        """.trimIndent()
    }
})