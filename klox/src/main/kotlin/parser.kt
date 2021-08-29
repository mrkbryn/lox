package com.mab.lox

import com.mab.lox.TokenType.*

class ParseError : RuntimeException()

class Parser(val tokens: List<Token>) {
    var current = 0

    private fun peek() = tokens[current]

    private fun previous() = tokens[current-1]

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd() = peek().type == EOF

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    private fun match(vararg types: TokenType): Boolean {
        types.forEach {
            if (check(it)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw error(peek(), message)
    }

    private fun error(token: Token, message: String): ParseError {
        Lox.error(token, message)
        return ParseError()
    }

    fun parse(): List<Stmt> {
        val statements = ArrayList<Stmt>()
        while (!isAtEnd()) {
            val stmt = declaration()
            if (stmt != null) {
                statements.add(stmt)
            }
        }
        return statements
    }

    private fun declaration(): Stmt? {
        try {
            if (match(CLASS)) return null  // TODO
            if (match(FUN)) return null  // TODO
            if (match(VAR)) return null  // TODO
            return statement()
        } catch (error: ParseError) {
            synchronize()
            return null
        }
    }

    private fun statement(): Stmt? {
        if (match(FOR)) return null
        if (match(IF)) return null
        if (match(PRINT)) return printStatement()
        if (match(RETURN)) return null
        if (match(WHILE)) return null
        if (match(LEFT_BRACE)) return null
        return expressionStatement()
    }

    private fun printStatement(): Stmt {
        val value = expression()
        consume(SEMICOLON, "Expect ';' after value.")
        return Stmt.Print(value)
    }

    private fun expressionStatement(): Stmt? {
        val expr = expression()
        consume(SEMICOLON, "Expect ';' after expression.")
        return null
//        return Stmt.Expression(expr)
    }

    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        val expr = or()

        return expr
    }

    private fun or(): Expr {
        var expr = and()

        while (match(OR)) {
            val operator = previous()
            val right = and()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
    }

    private fun and(): Expr {
        var expr = equality()

        while (match(AND)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
    }

    private fun equality(): Expr {
        // equality -> comparison ( ( "!=" | "==" comparison )* ;
        var expr = comparison()

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            val operator = previous()
            val right = comparison()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        // comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
        var expr = term()

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator = previous()
            val right = term()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun term(): Expr {
        // term -> factor ( ( "-" | "+" ) factor )* ;
        var expr = factor()

        while (match(MINUS, PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun factor(): Expr {
        // factor -> unary ( ( "/" | "*" ) unary )* ;
        var expr = unary()

        while (match(SLASH, STAR)) {
            val operator = previous()
            val right = unary()
            expr = Expr.Binary(expr, operator, right)
        }

        return expr
    }

    private fun unary(): Expr {
        if (match(BANG, BANG_EQUAL)) {
            val operator = previous()
            val right = unary()
            return Expr.Unary(operator, right)
        }

        return call()
    }

    private fun call(): Expr {
        val expr = primary()
        return expr
    }

    private fun primary(): Expr {
        // primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | "super" "." IDENTIFIER ;
        if (match(FALSE)) {
            return Expr.Literal(false)
        }
        if (match(TRUE)) {
            return Expr.Literal(true)
        }
        if (match(NIL)) {
            return Expr.Literal(null)
        }
        if (match(NUMBER, STRING)) {
            return Expr.Literal(previous().literal)
        }

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

    private fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return

            when (peek().type) {
                CLASS, FOR, FUN, IF, PRINT, RETURN, VAR, WHILE -> return
                else -> advance()
            }
        }
    }
}