package com.mab.lox.utils

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class EqualityTest : ShouldSpec({
    should("compare Booleans") {
        isEqual(a = true, b = true) shouldBe true
        isEqual(a = true, b = false) shouldBe false
        isEqual(a = false, b = true) shouldBe false
        isEqual(a = false, b = false) shouldBe true
    }

    should("compare Doubles") {
        isEqual(a = 1.5, b = 1.5) shouldBe true
        isEqual(a = 1.0, b = 2.0) shouldBe false
        isEqual(a = 1.0, b = 1.1) shouldBe false
    }

    should("compare Ints") {
        isEqual(a = 1, b = 1) shouldBe true
        isEqual(a = 10, b = 15) shouldBe false
    }

    should("compare Strings") {
        isEqual(a = "test123", b = "test123") shouldBe true
        isEqual(a = "foo", b = "bar") shouldBe false
    }

    should("compare nulls") {
        isEqual(a = null, b = null) shouldBe true
        isEqual(a = null, b = 5) shouldBe false
        isEqual(a = 5, b = null) shouldBe false
    }
})
