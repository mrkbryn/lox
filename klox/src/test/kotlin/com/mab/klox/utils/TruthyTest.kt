package com.mab.klox.utils

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class TruthyTest : ShouldSpec({
    should("evaluate to true for boolean True") {
        isTruthy(true) shouldBe true
    }

    should("evaluate to false for boolean False") {
        isTruthy(false) shouldBe false
    }

    should("evaluate to true for non-null object") {
        isTruthy(1) shouldBe true
        isTruthy(0) shouldBe true
    }

    should("evaluate to false for null object") {
        isTruthy(null) shouldBe false
    }
})
