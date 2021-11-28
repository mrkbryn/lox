package com.mab.klox.tools

import com.mab.klox.*
import com.mab.klox.scanner.Token
import com.mab.klox.scanner.TokenType

fun main() {
    val expression =
        Expr.Binary(
            left = Expr.Unary(
                operator = Token(
                    type = TokenType.MINUS,
                    lexeme = "-",
                    literal = null,
                    line = 1
                ),
                right = Expr.Literal(123)
            ),
            operator = Token(
                type = TokenType.STAR,
                lexeme = "*",
                literal = null,
                line = 1
            ),
            right = Expr.Grouping(
                expression = Expr.Literal(45.67)
            )
        )
    println(AstPrinter().print(expression))

    val statement = Stmt.Print(expression)
    println(AstPrinter().print(statement))
}
