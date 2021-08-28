package com.mab.lox

import com.mab.lox.TokenType.*

class ParseError : RuntimeException()

class Parser(val tokens: List<Token>) {
    var current = 0

    fun peek() = tokens[current]

    fun previous() = tokens[current-1]

    fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    fun isAtEnd() = peek().type == EOF

    fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    fun match(vararg types: TokenType): Boolean {
        types.forEach {
            if (check(it)) {
                advance()
                return true
            }
        }
        return false
    }

    fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw error(peek(), message)
    }

    fun error(token: Token, message: String): ParseError {
        error(token, message)
        return ParseError()
    }

    fun parse(): Expr {
        return expression()
    }

    private fun expression(): Expr {
        return primary()
    }

    private fun primary(): Expr {
        // primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | "super" "." IDENTIFIER ;
        if (match(FALSE)) return Expr.Literal(false)
        if (match(TRUE)) return Expr.Literal(true)
        if (match(NIL)) return Expr.Literal(null)
        if (match(NUMBER, STRING)) return Expr.Literal(previous().literal)

        // TODO SUPER
        // TODO THIS
        // TODO IDENTIFIER

        if (match(LEFT_PAREN)) {
            val expr = expression()
            consume(RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        throw error(peek(), "Expect expression.")
    }
}