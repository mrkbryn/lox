package com.mab.lox

import com.mab.lox.TokenType.*
import kotlin.collections.ArrayList

const val MAX_ARGUMENT_COUNT = 255

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
 *      returnStmt      -> "return" expression? ";"
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
 *      term            -> factor ( ( "-" | "+" ) factor )* ;
 *      factor          -> unary ( ( "/" | "*" ) unary )* ;
 *      unary           -> ( "!" | "-" ) unary | call ;
 *      call            -> primary ( "(" arguments? ")" | "." IDENTIFIER )* ;
 *      primary         -> "true" | "false" | "nil" | "this" | NUMBER | STRING | IDENTIFIER | "(" expression ")"
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

    private fun check(type: TokenType): Boolean {
        if (isAtEnd()) return false
        return peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd() = peek().type == EOF

    private fun peek() = tokens[current]

    private fun previous() = tokens[current-1]

    private fun error(token: Token, message: String): ParseError {
        Lox.error(token, message)
        return ParseError()
    }

    /**
     * `program -> declaration* EOF ;`
     */
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

    /**
     * `expression -> assignment ;`
     */
    private fun expression(): Expr {
        // expression -> assignment ;
        return assignment()
    }

    /**
     * `declaration -> classDecl | funDecl | varDecl | statement ;`
     */
    private fun declaration(): Stmt? {
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

    /**
     * `classDecl -> "class" IDENTIFIER ( "<" IDENTIFIER )? "{" function* "}" ;`
     */
    private fun classDeclaration(): Stmt {
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

    /**
     * `statement -> exprStmt | forStmt | ifStmt | printStmt | returnStmt | whileStmt | block ;`
     */
    private fun statement(): Stmt {
        return when {
            match(FOR) -> forStatement()
            match(IF) -> ifStatement()
            match(PRINT) -> printStatement()
            match(RETURN) -> returnStatement()
            match(WHILE) -> whileStatement()
            match(LEFT_BRACE) -> Stmt.Block(block())
            else -> expressionStatement()
        }
    }

    /**
     * `forStmt -> "for" "(" ( varDecl | exprStmt | ";" ) expression? ";" expression? ") statement ;`
     */
    private fun forStatement(): Stmt {
        consume(LEFT_PAREN, "Expect '(' after 'for'.")

        // Parse optional initializer statement.
        val initializer = when {
            match(SEMICOLON) -> null
            match(VAR) -> varDeclaration()
            else -> expressionStatement()
        }

        // Parse optional condition statement.
        var condition = if (!check(SEMICOLON)) expression() else null
        consume(SEMICOLON, "Expect ';' after loop condition.")

        // Parse optional increment statement.
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

        if (initializer != null) {
            body = Stmt.Block(listOf(initializer, body))
        }

        return body
    }

    /**
     * `ifStmt -> "if" "(" expression ")" statement ( "else" statement )? ;`
     */
    private fun ifStatement(): Stmt {
        consume(LEFT_PAREN, "Expect '(' after 'if'.")
        val condition = expression()
        consume(RIGHT_PAREN, "Expect ')' after if condition.")

        val thenBranch = statement()
        val elseBranch = if (match(ELSE)) statement() else null
        return Stmt.If(condition, thenBranch, elseBranch)
    }

    /**
     * `printStmt -> "print" expression ";" ;`
     */
    private fun printStatement(): Stmt {
        // TODO: desugar into standard lib print?
        val value = expression()
        consume(SEMICOLON, "Expect ';' after value.")
        return Stmt.Print(value)
    }

    /**
     * `returnStmt -> "return" expression? ";"`
     */
    private fun returnStatement(): Stmt {
        val keyword = previous()
        val value = if (!check(SEMICOLON)) expression() else null
        consume(SEMICOLON, "Expect ';' after return value.")
        return Stmt.Return(keyword, value)
    }

    /**
     * `varDecl -> "var" IDENTIFIER ( "=" expression )? ";" ;`
     */
    private fun varDeclaration(): Stmt {
        val name = consume(IDENTIFIER, "Expect variable name.")
        val initializer: Expr? = if (match(EQUAL)) expression() else null
        consume(SEMICOLON, "Expect ';' after variable declaration.")
        return Stmt.Var(name, initializer)
    }

    /**
     * `whileStmt -> "while" "(" expression ")" statement ;`
     */
    private fun whileStatement(): Stmt {
        consume(LEFT_PAREN, "Expect '(' after 'while'.")
        val condition = expression()
        consume(RIGHT_PAREN, "Expect ')' after condition.")
        val body = statement()
        return Stmt.While(condition, body)
    }

    /**
     * `exprStmt -> expression ";" ;`
     */
    private fun expressionStatement(): Stmt {
        val expr = expression()
        consume(SEMICOLON, "Expect ';' after expression.")
        return Stmt.Expression(expr)
    }

    /**
     * `function -> IDENTIFIER "(" parameters? ")" block ;`
     */
    private fun function(kind: String): Stmt.Function {
        val name = consume(IDENTIFIER, "Expect $kind name.")
        consume(LEFT_PAREN, "Expect '(' after $kind name.")
        val parameters = ArrayList<Token>()
        if (!check(RIGHT_PAREN)) {
            do {
                if (parameters.size >= MAX_ARGUMENT_COUNT) {
                    error(peek(), "Can't have more than $MAX_ARGUMENT_COUNT parameters.")
                }
                parameters.add(consume(IDENTIFIER, "Expect parameter name."))
            } while (match(COMMA))
        }
        consume(RIGHT_PAREN, "Expect ')' after parameters.")
        consume(LEFT_BRACE, "Expect '{' before $kind body.")
        val body = block()
        return Stmt.Function(name, parameters, body)
    }

    /**
     * `block -> "{" declaration* "}" ;`
     */
    private fun block(): List<Stmt> {
        val statements = ArrayList<Stmt>()
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            declaration()?.let { statements.add(it) }
        }
        consume(RIGHT_BRACE, "Expect '}' after block.")
        return statements
    }

    /**
     * `assignment -> ( call "." )? IDENTIFIER "=" assignment | logic_or ;`
     */
    private fun assignment(): Expr {
        val expr = or()

        if (match(EQUAL)) {
            val equals = previous()
            val value = assignment()
            when (expr) {
                is Expr.Variable -> return Expr.Assign(expr.name, value)
                is Expr.Get -> return Expr.Set(expr.obj, expr.name, value)
                else -> error(equals, "Invalid assignment target.")
            }
        }

        return expr
    }

    /**
     * `logic_or -> logic_and ( "or" logic_and )* ;`
     */
    private fun or(): Expr {
        var expr = and()
        while (match(OR)) expr = Expr.Logical(expr, previous(), and())
        return expr
    }

    /**
     * `logic_and -> equality ( "and" equality )* ;`
     */
    private fun and(): Expr {
        var expr = equality()
        while (match(AND))
            expr = Expr.Logical(expr, previous(), equality())
        return expr
    }

    /**
     * `equality -> comparison ( ( "!=" | "==" ) comparison )* ;`
     */
    private fun equality(): Expr {
        var expr = comparison()
        while (match(BANG_EQUAL, EQUAL_EQUAL))
            expr = Expr.Binary(expr, previous(), comparison())
        return expr
    }

    /**
     * `comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;`
     */
    private fun comparison(): Expr {
        var expr = term()
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL))
            expr = Expr.Binary(expr, previous(), term())
        return expr
    }

    /**
     * `term -> factor ( ( "-" | "+" ) factor )* ;`
     */
    private fun term(): Expr {
        var expr = factor()
        while (match(MINUS, PLUS))
            expr = Expr.Binary(expr, previous(), factor())
        return expr
    }

    /**
     * `factor -> unary ( ( "/" | "*" ) unary )* ;`
     */
    private fun factor(): Expr {
        var expr = unary()
        while (match(SLASH, STAR))
            expr = Expr.Binary(expr, previous(), unary())
        return expr
    }

    /**
     * `unary -> ( "!" | "-" ) unary | call ;`
     */
    private fun unary(): Expr {
        if (match(BANG, BANG_EQUAL))
            return Expr.Unary(previous(), unary())
        return call()
    }

    private fun finishCall(callee: Expr): Expr {
        val arguments = ArrayList<Expr>()
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size >= MAX_ARGUMENT_COUNT) {
                    error(peek(), "Can't have more than $MAX_ARGUMENT_COUNT arguments.")
                }
                arguments.add(expression())
            } while (match(COMMA))
        }
        val paren = consume(RIGHT_PAREN, "Expect ')' after arguments.")
        return Expr.Call(callee, paren, arguments)
    }

    /**
     * `call -> primary ( "(" arguments? ")" | "." IDENTIFIER )* ;`
     */
    private fun call(): Expr {
        var expr = primary()

        while (true) {
            when {
                match(LEFT_PAREN) -> {
                    expr = finishCall(expr)
                }
                match(DOT) -> {
                    val name = consume(IDENTIFIER, "Expect property name after '.'.")
                    expr = Expr.Get(expr, name)
                }
                else -> {
                    break
                }
            }
        }

        return expr
    }

    /**
     * `primary -> "true" | "false" | "nil" | "this" | NUMBER | STRING | IDENTIFIER | "(" expression ")"
     *           | "super" "." IDENTIFIER ;
     */
    private fun primary(): Expr {
        when {
            match(FALSE) -> return Expr.Literal(false)
            match(TRUE) -> return Expr.Literal(true)
            match(NIL) -> return Expr.Literal(null)
            match(NUMBER, STRING) -> return Expr.Literal(previous().literal)
            match(SUPER) -> {
                val keyword = previous()
                consume(DOT, "Expect '.' after 'super'.")
                val method = consume(IDENTIFIER, "Expect superclass method name.")
                return Expr.Super(keyword, method)
            }
            match(THIS) -> return Expr.This(previous())
            match(IDENTIFIER) -> return Expr.Variable(previous())
            match(LEFT_PAREN) -> {
                val expr = expression()
                consume(RIGHT_PAREN, "Expect ')' after expression.")
                return Expr.Grouping(expr)
            }
            else -> {
                // Throw ParseError.
                throw error(peek(), "Expect expression.")
            }
        }
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
