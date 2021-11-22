package com.mab.lox.utils

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class StringifyTest : ShouldSpec({
    should("format null values") {
        stringify(null) shouldBe "nil"
    }

    should("format Double values") {
        stringify(5.0) shouldBe "5"
    }

    should("format String values") {
        stringify("foobar") shouldBe "foobar"
    }
})
