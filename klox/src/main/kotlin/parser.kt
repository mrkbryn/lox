package com.mab.lox

import com.mab.lox.TokenType.*

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
        types.forEach { tokenType ->
            if (check(tokenType)) {
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
        val statements = mutableListOf<Stmt>()
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
    private fun expression(): Expr = assignment()

    /**
     * `declaration -> classDecl | funDecl | varDecl | statement ;`
     */
    private fun declaration(): Stmt? {
        return try {
            when {
                match(CLASS) -> classDeclaration()
                match(FUN) -> function("function")
                match(VAR) -> varDeclaration()
                else -> statement()
            }
        } catch (error: ParseError) {
            synchronize()
            null
        }
    }

    /**
     * `classDecl -> "class" IDENTIFIER ( "<" IDENTIFIER )? "{" function* "}" ;`
     */
    private fun classDeclaration(): Stmt {
        val name = consume(IDENTIFIER, "Expect class name.")

        // Parse optional superclass.
        val superclass: Expr.Variable? = when {
            match(LESS) -> {
                consume(IDENTIFIER, "Expect superclass name.")
                Expr.Variable(
                    name = previous()
                )
            }
            else -> null
        }

        // Parse class body.
        consume(LEFT_BRACE, "Expect '{' before class body.")
        val methods = mutableListOf<Stmt.Function>()
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"))
        }
        consume(RIGHT_BRACE, "Expect '}' after class body.")

        return Stmt.Class(
            name = name,
            superclass = superclass,
            methods = methods
        )
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
            match(LEFT_BRACE) -> Stmt.Block(statements = block())
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
            body =
                Stmt.Block(
                    statements = listOf(
                        body,
                        Stmt.Expression(increment)
                    )
                )
        }

        if (condition == null) {
            condition = Expr.Literal(value = true)
        }
        body = Stmt.While(
            condition = condition,
            body = body
        )

        if (initializer != null) {
            body =
                Stmt.Block(
                    statements = listOf(initializer, body)
                )
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
        return Stmt.If(
            condition = condition,
            thenBranch = thenBranch,
            elseBranch = elseBranch
        )
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
        return Stmt.Return(
            keyword = keyword,
            value = value
        )
    }

    /**
     * `varDecl -> "var" IDENTIFIER ( "=" expression )? ";" ;`
     */
    private fun varDeclaration(): Stmt {
        val name = consume(IDENTIFIER, "Expect variable name.")
        val initializer: Expr? = if (match(EQUAL)) expression() else null
        consume(SEMICOLON, "Expect ';' after variable declaration.")
        return Stmt.Var(
            name = name,
            initializer = initializer
        )
    }

    /**
     * `whileStmt -> "while" "(" expression ")" statement ;`
     */
    private fun whileStatement(): Stmt {
        consume(LEFT_PAREN, "Expect '(' after 'while'.")
        val condition = expression()
        consume(RIGHT_PAREN, "Expect ')' after condition.")
        val body = statement()
        return Stmt.While(
            condition = condition,
            body = body
        )
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
        val parameters = mutableListOf<Token>()
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
        return Stmt.Function(
            name = name,
            params = parameters,
            body = body
        )
    }

    /**
     * `block -> "{" declaration* "}" ;`
     */
    private fun block(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
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
                is Expr.Variable -> return Expr.Assign(
                    name = expr.name,
                    value = value
                )
                is Expr.Get -> return Expr.Set(
                    obj = expr.obj,
                    name = expr.name,
                    value = value
                )
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
        while (match(OR)) {
            expr = Expr.Logical(
                left = expr,
                operator = previous(),
                right = and()
            )
        }
        return expr
    }

    /**
     * `logic_and -> equality ( "and" equality )* ;`
     */
    private fun and(): Expr {
        var expr = equality()
        while (match(AND)) {
            expr = Expr.Logical(
                left = expr,
                operator = previous(),
                right = equality()
            )
        }
        return expr
    }

    /**
     * `equality -> comparison ( ( "!=" | "==" ) comparison )* ;`
     */
    private fun equality(): Expr {
        var expr = comparison()
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            expr = Expr.Binary(
                left = expr,
                operator = previous(),
                right = comparison()
            )
        }
        return expr
    }

    /**
     * `comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;`
     */
    private fun comparison(): Expr {
        var expr = term()
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            expr = Expr.Binary(
                left = expr,
                operator = previous(),
                right = term()
            )
        }
        return expr
    }

    /**
     * `term -> factor ( ( "-" | "+" ) factor )* ;`
     */
    private fun term(): Expr {
        var expr = factor()
        while (match(MINUS, PLUS)) {
            expr = Expr.Binary(
                left = expr,
                operator = previous(),
                right = factor()
            )
        }
        return expr
    }

    /**
     * `factor -> unary ( ( "/" | "*" ) unary )* ;`
     */
    private fun factor(): Expr {
        var expr = unary()
        while (match(SLASH, STAR)) {
            expr = Expr.Binary(
                left = expr,
                operator = previous(),
                right = unary()
            )
        }
        return expr
    }

    /**
     * `unary -> ( "!" | "-" ) unary | call ;`
     */
    private fun unary(): Expr {
        if (match(BANG, BANG_EQUAL))
            return Expr.Unary(
                operator = previous(),
                right = unary()
            )
        return call()
    }

    private fun finishCall(callee: Expr): Expr {
        val arguments = mutableListOf<Expr>()
        if (!check(RIGHT_PAREN)) {
            do {
                if (arguments.size >= MAX_ARGUMENT_COUNT) {
                    error(peek(), "Can't have more than $MAX_ARGUMENT_COUNT arguments.")
                }
                arguments.add(expression())
            } while (match(COMMA))
        }
        val paren = consume(RIGHT_PAREN, "Expect ')' after arguments.")
        return Expr.Call(
            callee = callee,
            paren = paren,
            arguments = arguments
        )
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
                    expr = Expr.Get(
                        obj = expr,
                        name = name
                    )
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
        return when {
            match(FALSE) -> Expr.Literal(value = false)
            match(TRUE) -> Expr.Literal(value = true)
            match(NIL) -> Expr.Literal(value = null)
            match(NUMBER, STRING) -> Expr.Literal(value = previous().literal)
            match(SUPER) -> {
                val keyword = previous()
                consume(DOT, "Expect '.' after 'super'.")
                val method = consume(IDENTIFIER, "Expect superclass method name.")
                Expr.Super(
                    keyword = keyword,
                    method = method
                )
            }
            match(THIS) -> Expr.This(previous())
            match(IDENTIFIER) -> Expr.Variable(previous())
            match(LEFT_PAREN) -> {
                val expr = expression()
                consume(RIGHT_PAREN, "Expect ')' after expression.")
                Expr.Grouping(expression = expr)
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
