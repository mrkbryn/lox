package com.mab.lox.scanner

import com.mab.lox.TokenType
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class ScannerTest : ShouldSpec({
    should("scan keyword identifiers") {
        val tokenTypes = Scanner("and class else false for fun if nil or print return super this true var while")
            .scanTokens()
            .map { it.type }
        tokenTypes shouldBe listOf(
            TokenType.AND,
            TokenType.CLASS,
            TokenType.ELSE,
            TokenType.FALSE,
            TokenType.FOR,
            TokenType.FUN,
            TokenType.IF,
            TokenType.NIL,
            TokenType.OR,
            TokenType.PRINT,
            TokenType.RETURN,
            TokenType.SUPER,
            TokenType.THIS,
            TokenType.TRUE,
            TokenType.VAR,
            TokenType.WHILE,
            TokenType.EOF // Always have EOF
        )
    }

    should("scan single-character tokens") {
        val tokenTypes = Scanner("( ) { } , . - + ; * ! != = == < <= > >= /")
            .scanTokens()
            .map { it.type }
        tokenTypes shouldBe listOf(
            TokenType.LEFT_PAREN,
            TokenType.RIGHT_PAREN,
            TokenType.LEFT_BRACE,
            TokenType.RIGHT_BRACE,
            TokenType.COMMA,
            TokenType.DOT,
            TokenType.MINUS,
            TokenType.PLUS,
            TokenType.SEMICOLON,
            TokenType.STAR,
            TokenType.BANG,
            TokenType.BANG_EQUAL,
            TokenType.EQUAL,
            TokenType.EQUAL_EQUAL,
            TokenType.LESS,
            TokenType.LESS_EQUAL,
            TokenType.GREATER,
            TokenType.GREATER_EQUAL,
            TokenType.SLASH,
            TokenType.EOF // Always have EOF
        )
    }

    should("scan Doubles") {
        val tokens = Scanner("5.6")
            .scanTokens()
        tokens[0].type shouldBe TokenType.NUMBER
        tokens[0].literal shouldBe 5.6
    }

    should("scan Ints") {
        val tokens = Scanner("10")
            .scanTokens()
        tokens[0].type shouldBe TokenType.NUMBER
        tokens[0].literal shouldBe 10
    }
})
