package com.mab.klox.integration

import com.mab.klox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class AssignmentTests : ShouldSpec({
    should("handle associativity") {
        val source = """
            var a = "a";
            var b = "b";
            var c = "c";
            
            a = b = c;
            print a;
            print b;
            print c;
        """.trimIndent()
        runScript(source) shouldBe """
            c
            c
            c
            
        """.trimIndent()
    }

    should("handle global assignment") {
        val source = """
            var a = "before";
            print a;
            
            a = "after";
            print a;
            
            print a = "arg";
            print a;
        """.trimIndent()
        runScript(source) shouldBe """
            before
            after
            arg
            arg
            
        """.trimIndent()
    }

    should("disallow assignment to grouping") {
        val source = """
            var a = "a";
            (a) = "value";
        """.trimIndent()
        runScript(source) shouldBe """
            [line 2] Error at '=': Invalid assignment target.
            
        """.trimIndent()
    }

    should("infix operator") {
        val source = """
            var a = "a";
            var b = "b";
            a + b = "value";
        """.trimIndent()
        runScript(source) shouldBe """
            [line 3] Error at '=': Invalid assignment target.
            
        """.trimIndent()
    }

    should("handle local variable assignment") {
        val source = """
            {
                var a = "before";
                print a;
                
                a = "after";
                print a;
                
                print a = "arg";
                print a;
            }
        """.trimIndent()
        runScript(source) shouldBe """
            before
            after
            arg
            arg
            
        """.trimIndent()
    }

    should("error on prefix operator") {
        val source = """
            var a = "a";
            !a = "value";
        """.trimIndent()
        runScript(source) shouldBe """
            [line 2] Error at '=': Invalid assignment target.
            
        """.trimIndent()
    }

    should("handle basic assignment syntax") {
        val source = """
            var a = "before";
            var c = a = "var";
            print a;
            print c;
        """.trimIndent()
        runScript(source) shouldBe """
            var
            var
            
        """.trimIndent()
    }

    should("handle this assignment rules") {
        val source = """
            class Foo {
                Foo() {
                    this = "value";
                }
            }
            
            Foo();
        """.trimIndent()
        runScript(source) shouldBe """
            [line 3] Error at '=': Invalid assignment target.
            
        """.trimIndent()
    }

    should("error on undefined variable") {
        val source = """
            unknown = "what";
        """.trimIndent()
        runScript(source) shouldBe """
            Undefined variable 'unknown'. 
            [line 1]
            
        """.trimIndent()
    }

    should("handle += operator") {
        val source = """
            var x = 1;
            x += 5;
            print x;
            
            var s = "hello";
            s += ", ";
            s += "world!";
            print s;
        """.trimIndent()
        runScript(source) shouldBe """
            6
            hello, world!
            
        """.trimIndent()
    }

    should("handle -= operator") {
        val source = """
            var x = 10;
            x -= 2;
            print x;
        """.trimIndent()
        runScript(source) shouldBe """
            8
            
        """.trimIndent()
    }

    should("check operands for += operator") {
        val source = """
            var x = 10;
            x += "hello, world!";
        """.trimIndent()
        runScript(source) shouldBe """
            Operands must be two numbers or two strings. 
            [line 2]
            
        """.trimIndent()
    }

    should("check operands for -= operator") {
        val source = """
            var x = 10;
            x -= "hello, world!";
        """.trimIndent()
        runScript(source) shouldBe """
            Operand(s) must be numbers. 
            [line 2]
            
        """.trimIndent()
    }

    should("disallow chained += operators") {
        val source = """
            var x = 10;
            x += 1 += 2;
        """.trimIndent()
        runScript(source) shouldBe """
            [line 2] Error at '+=': Invalid assignment target.
            
        """.trimIndent()
    }
})
