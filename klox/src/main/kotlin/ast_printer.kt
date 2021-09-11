package com.mab.lox

import java.lang.StringBuilder

class AstPrinter : Expr.Visitor<String> {

    fun print(expr: Expr) = expr.accept(this)

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        exprs.forEach {
            builder.append(" ")
            builder.append(it.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }

    override fun visitBinaryExpr(expr: Expr.Binary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): String {
        if (expr.value == null) {
            return "nil"
        }
        return expr.value.toString()
    }

    override fun visitUnaryExpr(expr: Expr.Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    override fun visitLogicalExpr(expr: Expr.Logical): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitVariableExpr(expr: Expr.Variable): String {
        return expr.name.lexeme
    }

    override fun visitAssignExpr(expr: Expr.Assign): String {
        return parenthesize("${expr.name.lexeme} := ", expr.value)
    }

    override fun visitCallExpr(expr: Expr.Call): String {
        TODO("Not yet implemented")
    }

    override fun visitGetExpr(expr: Expr.Get): String {
        TODO("Not yet implemented")
    }

    override fun visitSetExpr(expr: Expr.Set): String {
        TODO("Not yet implemented")
    }

    override fun visitSuperExpr(expr: Expr.Super): String {
        TODO("Not yet implemented")
    }

    override fun visitThisExpr(expr: Expr.This): String {
        TODO("Not yet implemented")
    }
}

fun main() {
    val expression = Expr.Binary(
        Expr.Unary(
            Token(TokenType.MINUS, "-", null, 1),
            Expr.Literal(123)),
        Token(TokenType.STAR, "*", null, 1),
        Expr.Grouping(Expr.Literal(45.67)))

    println(AstPrinter().print(expression))
}
