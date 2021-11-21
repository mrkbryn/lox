package com.mab.lox

import java.lang.StringBuilder

/**
 * Visits a Lox abstract syntax tree and generates a string representation of Expr and Stmts.
 */
class AstPrinter : Expr.Visitor<String>, Stmt.Visitor<String> {

    fun print(expr: Expr) = expr.accept(this)

    fun print(stmt: Stmt) = stmt.accept(this)

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

    private fun parenthesize2(name: String, vararg parts: Any?): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        transform(builder, parts)
        builder.append(")")
        return builder.toString()
    }

    private fun transform(builder: StringBuilder, vararg parts: Any?) {
        parts.forEach {
            builder.append(" ")
            when (it) {
                is Expr -> it.accept(this)
                is Stmt -> it.accept(this)
                is Token -> builder.append(it.lexeme)
                else -> builder.append(it)
            }
        }
    }

    //
    // Expr.Visitor<String> Interface Implementation.
    //

    override fun visitAssignExpr(expr: Expr.Assign): String = parenthesize("${expr.name.lexeme} := ", expr.value)

    override fun visitBinaryExpr(expr: Expr.Binary): String = parenthesize(expr.operator.lexeme, expr.left, expr.right)

    override fun visitCallExpr(expr: Expr.Call): String = parenthesize2("call", expr.callee, expr.arguments)

    override fun visitGetExpr(expr: Expr.Get): String = parenthesize2(".", expr.obj, expr.name.lexeme)

    override fun visitGroupingExpr(expr: Expr.Grouping): String = parenthesize("group", expr.expression)

    override fun visitLiteralExpr(expr: Expr.Literal): String = if (expr.value == null) "nil" else expr.value.toString()

    override fun visitLogicalExpr(expr: Expr.Logical): String = parenthesize(expr.operator.lexeme, expr.left, expr.right)

    override fun visitSetExpr(expr: Expr.Set): String = parenthesize2("=", expr.obj, expr.name.lexeme, expr.value)

    override fun visitSuperExpr(expr: Expr.Super): String = parenthesize2("super", expr.method)

    override fun visitThisExpr(expr: Expr.This): String = "this"

    override fun visitUnaryExpr(expr: Expr.Unary): String = parenthesize(expr.operator.lexeme, expr.right)

    override fun visitVariableExpr(expr: Expr.Variable): String = expr.name.lexeme

    //
    // Stmt.Visitor<String> Interface Implementation.
    //

    override fun visitBlockStmt(stmt: Stmt.Block): String {
        val builder = StringBuilder()
        builder.append("(block ")
        stmt.statements.forEach { builder.append(it.accept(this)) }
        builder.append(")")
        return builder.toString()
    }

    override fun visitClassStmt(stmt: Stmt.Class): String {
        val builder = StringBuilder()
        builder.append("(class ${stmt.name.lexeme}")
        if (stmt.superclass != null) builder.append(" < ${print(stmt.superclass)}")

        stmt.methods.forEach { builder.append(" ${print(it)}") }

        builder.append(")")
        return builder.toString()
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): String = parenthesize(";", stmt.expression)

    override fun visitFunctionStmt(stmt: Stmt.Function): String {
        val builder = StringBuilder()
        builder.append("(fun ${stmt.name.lexeme}(")
        stmt.params.forEach {
            if (it != stmt.params[0]) builder.append(" ")
            builder.append(it.lexeme)
        }
        builder.append(") ")
        stmt.body.forEach { builder.append(it.accept(this)) }
        builder.append(")")
        return builder.toString()
    }

    override fun visitIfStmt(stmt: Stmt.If): String {
        if (stmt.elseBranch == null) {
            return parenthesize2("if", stmt.condition, stmt.thenBranch)
        }
        return parenthesize2("if-else", stmt.condition, stmt.thenBranch, stmt.elseBranch)
    }

    override fun visitPrintStmt(stmt: Stmt.Print): String = parenthesize("print", stmt.expression)

    override fun visitReturnStmt(stmt: Stmt.Return): String {
        return stmt.value?.let { parenthesize("return", it) } ?: "(return)"
    }

    override fun visitVarStmt(stmt: Stmt.Var): String {
        if (stmt.initializer == null) {
            return parenthesize2("var", stmt.name)
        }
        return parenthesize2("var", stmt.name, "=", stmt.initializer)
    }

    override fun visitWhileStmt(stmt: Stmt.While): String = parenthesize2("while", stmt.condition, stmt.body)
}
