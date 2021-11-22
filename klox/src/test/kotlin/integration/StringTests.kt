package integration

import com.mab.lox.runScript
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class StringTests : ShouldSpec({
    should("parse and print strings") {
        runScript("print \"foobar\";") shouldBe "foobar\n"
    }

    should("concatenate strings") {
        runScript("print \"foo\" + \"bar\" + \"bar\";") shouldBe "foobarbar\n"
    }
})
