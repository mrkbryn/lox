package com.mab.lox

import java.util.*

class Resolver(val interpreter: Interpreter) : Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private val scopes: Stack<Map<String, Boolean>> = Stack()
    private var currentFunction = FunctionType.NONE;
    private var currentClass = ClassType.NONE;

    private enum class FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD,
    }

    private enum class ClassType {
        NONE,
        CLASS,
        SUBCLASS,
    }

    fun resolve(statements: List<Stmt>) = statements.forEach { resolve(it) }

    fun resolve(stmt: Stmt) = stmt.accept(this)

    fun resolve(expr: Expr) = expr.accept(this)

    //
    // Stmt.Visitor<Void> Interface Implementation.
    //

    override fun visitBlockStmt(stmt: Stmt.Block): Void {
        TODO("Not yet implemented")
    }

    override fun visitClassStmt(stmt: Stmt.Class): Void {
        TODO("Not yet implemented")
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Void {
        TODO("Not yet implemented")
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Void {
        TODO("Not yet implemented")
    }

    override fun visitIfStmt(stmt: Stmt.If): Void {
        TODO("Not yet implemented")
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Void {
        TODO("Not yet implemented")
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Void {
        TODO("Not yet implemented")
    }

    override fun visitVarStmt(stmt: Stmt.Var): Void {
        TODO("Not yet implemented")
    }

    override fun visitWhileStmt(stmt: Stmt.While): Void {
        TODO("Not yet implemented")
    }

    //
    // Expr.Visitor<Void> Interface Implementation.
    //

    override fun visitAssignExpr(expr: Expr.Assign): Void {
        TODO("Not yet implemented")
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Void {
        TODO("Not yet implemented")
    }

    override fun visitCallExpr(expr: Expr.Call): Void {
        TODO("Not yet implemented")
    }

    override fun visitGetExpr(expr: Expr.Get): Void {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Void {
        TODO("Not yet implemented")
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Void {
        TODO("Not yet implemented")
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Void {
        TODO("Not yet implemented")
    }

    override fun visitSetExpr(expr: Expr.Set): Void {
        TODO("Not yet implemented")
    }

    override fun visitSuperExpr(expr: Expr.Super): Void {
        TODO("Not yet implemented")
    }

    override fun visitThisExpr(expr: Expr.This): Void {
        TODO("Not yet implemented")
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Void {
        TODO("Not yet implemented")
    }

    override fun visitVariableExpr(expr: Expr.Variable): Void {
        TODO("Not yet implemented")
    }
}
