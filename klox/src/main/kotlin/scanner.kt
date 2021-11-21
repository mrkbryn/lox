package com.mab.lox

import com.mab.lox.TokenType.*

class Scanner(private val source: String) {
    private val tokens = ArrayList<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    private val keywords = mapOf(
        "and" to AND, "class" to CLASS, "else" to ELSE, "false" to FALSE, "for" to FOR, "fun" to FUN, "if" to IF,
        "nil" to NIL, "or" to OR, "print" to PRINT, "return" to RETURN, "super" to SUPER, "this" to THIS,
        "true" to TRUE, "var" to VAR, "while" to WHILE
    )

    private fun isAtEnd() = current >= source.length

    private fun isAlpha(c: Char) = (c in 'a'..'z') || (c in 'A'..'Z') || c == '_'

    private fun isDigit(c: Char) = c in '0'..'9'

    private fun isAlphaNumeric(c: Char) = isAlpha(c) || isDigit(c)

    private fun peek(): Char {
        if (isAtEnd()) return 0.toChar()
        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return 0.toChar()
        return source[current + 1]
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false

        // Matched expected. Consume next character.
        advance()
        return true
    }

    private fun advance(): Char {
        return source[current++]
    }

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        tokens.add(Token(EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        when (val c = advance()) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
            '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else {
                    addToken(SLASH)
                }
            }
            ' ', '\r', '\t' -> {} // Ignore whitespace.
            '\n' -> line++
            '"' -> string()
            else -> {
                if (isDigit(c)) {
                    number()
                } else if (isAlpha(c)) {
                    identifier()
                } else {
                    Lox.error(line, "Unexpected character.")
                }

            }
        }
    }

    /**
     * `STRING -> "\"" <any char except "\"">* "\"";`
     */
    private fun string() {
        // Handler for parsing remaining portion of string.
        while (peek() != '"' && !isAtEnd()) {
            // Ensure we count lines as we're reading strings.
            if (peek() == '\n') line++
            // Continue reading the string.
            advance()
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.")
            return
        }

        // Move past the closing ".
        advance()

        // Trim the surrounding quotes.
        val value = source.substring(start + 1, current - 1)
        addToken(STRING, value)
    }

    /**
     * `IDENTIFIER -> ALPHA ( ALPHA | DIGIT )* ;`
     */
    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()

        val text = source.substring(start, current)
        addToken(keywords.getOrDefault(text, IDENTIFIER))
    }

    /**
     * `NUMBER -> DIGIT+ ( "." DIGIT+ )? ;`
     */
    private fun number() {
        while (isDigit(peek())) advance()

        val isInt = peek() != '.'
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the '.'
            advance()
            // Parse remaining digits.
            while (isDigit(peek())) advance()
        }

        val literalStr = source.substring(start, current)
        val literal = if (isInt) literalStr.toInt() else literalStr.toDouble()
        addToken(NUMBER, literal)
    }
}
