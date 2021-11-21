package com.mab.lox.utils

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class EqualityTest : ShouldSpec({
    should("evaluate two Booleans") {
        isEqual(a = true, b = true) shouldBe true
        isEqual(a = true, b = false) shouldBe false
        isEqual(a = false, b = true) shouldBe false
        isEqual(a = false, b = false) shouldBe true
    }
})
