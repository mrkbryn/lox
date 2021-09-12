package com.mab.lox

import com.mab.lox.TokenType.*
import kotlin.collections.ArrayList

/**
 * Implementation of a recursive-descent parser for the Lox language. Takes as input a stream of Lox tokens and
 * generates an abstract syntax tree of Lox statements. The Parser follows the Lox grammar below.
 *
 * # Grammar:
 * ```
 *      program         -> declaration* EOF ;
 * ```
 *
 * ## Declarations:
 * ```
 *      declaration     -> classDecl
 *                      | funDecl
 *                      | varDecl
 *                      | statement ;
 *      classDecl       -> "class" IDENTIFIER ( "<" IDENTIFIER )? "{" function* "}" ;
 *      funDecl         -> "fun" function ;
 *      varDecl         -> "var" IDENTIFIER ( "=" expression )? ";" ;
 * ```
 *
 * ## Statements:
 * ```
 *      statement       -> exprStmt
 *                      | forStmt
 *                      | ifStmt
 *                      | printStmt
 *                      | returnStmt
 *                      | whileStmt
 *                      | block ;
 *      exprStmt        -> expression ";" ;
 *      forStmt         -> "for" "(" ( varDecl | exprStmt | ";" )
 *                          expression? ";"
 *                          expression? ") statement ;
 *      ifStmt          -> "if" "(" expression ")" statement ( "else" statement )? ;
 *      printStmt       -> "print" expression ";" ;
 *      returnStmt      -> "return expression? ";"
 *      whileStmt       -> "while" "(" expression ")" statement ;
 *      block           -> "{" declaration* "}" ;
 * ```
 *
 * ## Expressions:
 * ```
 *      expression      -> assignment ;
 *      assignment      -> ( call "." )? IDENTIFIER "=" assignment | logic_or ;
 *      logic_or        -> logic_and ( "or" logic_and )* ;
 *      logic_and       -> equality ( "and" equality )* ;
 *      equality        -> comparison ( ( "!=" | "==" ) comparison )* ;
 *      comparison      -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
 *      factor          -> unary ( ( "/" | "*" ) unary )* ;
 *      unary           -> ( "!" | "-" ) unary | call ;
 *      call            -> primary ( "(" arguments? ")" | "." IDENTIFIER )* ;
 *      primary         -> "true" | "false" | "nil" | "this" | NUMBER | STRING | IDENTIFIER | "(" expression ")" |
 *                      | "super" "." IDENTIFIER ;
 * ```
 *
 * ## Utility Rules:
 * ```
 *      function        -> IDENTIFIER "(" parameters? ")" block ;
 *      parameters      -> IDENTIFIER ( "," IDENTIFIER )* ;
 *      arguments       -> expression ( "," expression )* ;
 * ```
 *
 * ## Lexical Grammar:
 * ```
 *      NUMBER          -> DIGIT+ ( "." DIGIT+ )? ;
 *      STRING          -> "\"" <any char except "\"">* "\"";
 *      IDENTIFIER      -> ALPHA ( ALPHA | DIGIT )* ;
 *      ALPHA           -> "a" ... "z" | "A" ... "Z" | "_" ;
 *      DIGIT           -> "0" ... "9" ;
 * ```
 */
class Parser(val tokens: List<Token>) {
    private var current = 0

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
        // declaration -> classDecl | funDecl | varDecl | statement ;
        try {
            if (match(CLASS)) return classDeclaration()
            if (match(FUN)) return function("function")
            if (match(VAR)) return varDeclaration()
            return statement()
        } catch (error: ParseError) {
            synchronize()
            return null
        }
    }

    private fun classDeclaration(): Stmt {
        // classDecl -> "class" IDENTIFIER ( "<" IDENTIFIER )? "{" function* "}" ;
        val name = consume(IDENTIFIER, "Expect class name.")

        // Parse optional superclass.
        var superclass: Expr.Variable? = null
        if (match(LESS)) {
            consume(IDENTIFIER, "Expect superclass name.")
            superclass = Expr.Variable(previous())
        }

        // Parse class body.
        consume(LEFT_BRACE, "Expect '{' before class body.")
        val methods = ArrayList<Stmt.Function>()
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"))
        }
        consume(RIGHT_BRACE, "Expect '}' after class body.")

        return Stmt.Class(name, superclass, methods)
    }

    private fun function(kind: String): Stmt.Function {
        val name = consume(IDENTIFIER, "Expect $kind name.")
        consume(LEFT_PAREN, "Expect '(' after $kind name.")
        val parameters = ArrayList<Token>()
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size >= 255) {
                    error(peek(), "Can't have more than 255 parameters.")
                }
                parameters.add(consume(IDENTIFIER, "Expect parameter name."))
            } while (match(COMMA))
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.")
        consume(LEFT_BRACE, "Expect '{' before $kind body.")
        val body = block()
        return Stmt.Function(name, parameters, body)
    }

    private fun varDeclaration(): Stmt {
        val name = consume(IDENTIFIER, "Expect variable name.")
        val initializer: Expr? = if (match(EQUAL)) expression() else null
        consume(SEMICOLON, "Expect ';' after variable declaration.")
        return Stmt.Var(name, initializer)
    }

    private fun statement(): Stmt {
        // statement -> ifStmt | printStmt | returnStmt | whileStmt | block | exprStmt ;
        if (match(FOR)) return forStatement()
        if (match(IF)) return ifStatement()
        if (match(PRINT)) return printStatement()
        if (match(RETURN)) return returnStatement()
        if (match(WHILE)) return whileStatement()
        if (match(LEFT_BRACE)) return Stmt.Block(block())
        return expressionStatement()
    }

    private fun forStatement(): Stmt {
        consume(LEFT_PAREN, "Expect '(' after 'for'.")

        val initializer: Stmt?
        if (match(SEMICOLON)) {
            initializer = null
        } else if (match(VAR)) {
            initializer = varDeclaration()
        } else {
            initializer = expressionStatement()
        }

        var condition = if (!check(SEMICOLON)) expression() else null
        consume(SEMICOLON, "Expect ';' after loop condition.")

        val increment = if (!check(RIGHT_PAREN)) expression() else null
        consume(RIGHT_PAREN, "Expect ')' after for clauses.")

        // Desugar into while loop.
        var body = statement()
        if (increment != null) {
            body = Stmt.Block(listOf(body, Stmt.Expression(increment)))
        }

        if (condition == null) {
            condition = Expr.Literal(true)
        }
        body = Stmt.While(condition, body)

        return body
    }

    private fun ifStatement(): Stmt {
        // ifStmt -> "if" "(" expression ")" statement ( "else" statement )? ;
        consume(LEFT_PAREN, "Expect '(' after 'if'.")
        val condition = expression()
        consume(RIGHT_PAREN, "Expect ')' after if condition.")

        val thenBranch = statement()
        val elseBranch = if (match(ELSE)) statement() else null
        return Stmt.If(condition, thenBranch, elseBranch)
    }

    private fun printStatement(): Stmt {
        // printStmt -> "print" expression
        val value = expression()
        consume(SEMICOLON, "Expect ';' after value.")
        return Stmt.Print(value)
    }

    private fun returnStatement(): Stmt {
        // returnStmt -> "return" ( expression )? ;
        val keyword = previous()
        val value = if (!check(SEMICOLON)) expression() else null
        consume(SEMICOLON, "Expect ';' after return value.")
        return Stmt.Return(keyword, value)
    }

    private fun whileStatement(): Stmt {
        // whileStmt -> "while" "(" expression ")" statement ;
        consume(LEFT_PAREN, "Expect '(' after 'while'.")
        val condition = expression()
        consume(RIGHT_PAREN, "Expect ')' after condition.")
        val body = statement()
        return Stmt.While(condition, body)
    }

    private fun expressionStatement(): Stmt {
        // expressionStmt -> expression ";" ;
        val expr = expression()
        consume(SEMICOLON, "Expect ';' after expression.")
        return Stmt.Expression(expr)
    }

    private fun expression(): Expr {
        // expression -> assignment ;
        return assignment()
    }

    private fun block(): List<Stmt> {
        // block -> "{" ( declaration )* "}" ;
        val statements = ArrayList<Stmt>()
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            declaration()?.let { statements.add(it) }
        }
        consume(RIGHT_BRACE, "Expect '}' after block.")
        return statements
    }

    private fun assignment(): Expr {
        val expr = or()

        if (match(EQUAL)) {
            val equals = previous()
            val value = assignment()
            when (expr) {
                is Expr.Variable -> return Expr.Assign(expr.name, value)
                else -> error(equals, "Invalid assignment target.")
            }
        }

        return expr
    }

    private fun or(): Expr {
        // or -> and ( "or" and )*
        var expr = and()

        while (match(OR)) {
            val operator = previous()
            val right = and()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
    }

    private fun and(): Expr {
        // and -> equality ( "and" equality )*
        var expr = equality()

        while (match(AND)) {
            val operator = previous()
            val right = equality()
            expr = Expr.Logical(expr, operator, right)
        }

        return expr
    }

    private fun equality(): Expr {
        // equality -> comparison ( ( "!=" | "==" ) comparison )* ;
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
        var expr = primary()

        while (true) {
            if (match(LEFT_PAREN)) {
                expr = finishCall(expr)
            } else if (match(DOT)) {
                val name = consume(IDENTIFIER, "Expect property name after '.'.")
            } else {
                break
            }
        }

        return expr
    }

    private fun finishCall(callee: Expr): Expr {
        val arguments = ArrayList<Expr>()
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size >= 255) {
                    error(peek(), "Can't have more than 255 arguments.")
                }
                arguments.add(expression())
            } while (match(COMMA))
        }
        val paren = consume(RIGHT_PAREN, "Expect ')' after arguments.")
        return Expr.Call(callee, paren, arguments)
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

        if (match(SUPER)) {
            val keyword = previous()
            consume(DOT, "Expect '.' after 'super'.")
            val method = consume(IDENTIFIER, "Expect superclass method name.")
            return Expr.Super(keyword, method)
        }

        if (match(THIS)) return Expr.This(previous())

        if (match(IDENTIFIER)) {
            return Expr.Variable(previous())
        }

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
