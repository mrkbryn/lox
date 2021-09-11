package com.mab.lox

import com.mab.lox.TokenType.*

class Interpreter(var environment: Environment = Environment()) : Expr.Visitor<Any?>, Stmt.Visitor<Void?> {
    val globals: Environment
    val locals: Map<Expr, Int>

    init {
        globals = Environment()
        locals = HashMap()
    }

    fun interpret(statements: List<Stmt>) {
        try {
            statements.forEach {
                execute(it)
            }
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    private fun evaluate(expr: Expr) = expr.accept(this)

    private fun execute(stmt: Stmt) = stmt.accept(this)

    private fun executeBlock(statements: List<Stmt>, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment
            statements.forEach {
                execute(it)
            }
        } finally {
            this.environment = previous
        }
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)
        // TODO: implement locals
        environment.assign(expr.name, value)
        return value
    }

    override fun visitIfStmt(stmt: Stmt.If): Void? {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }
        return null
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Void? {
        executeBlock(stmt.statements, Environment(environment))
        return null
    }

    override fun visitWhileStmt(stmt: Stmt.While): Void? {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }
        return null
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            GREATER -> return (left as Double) > (right as Double)
            GREATER_EQUAL -> return (left as Double) >= (right as Double)
            LESS -> return (left as Double) < (right as Double)
            LESS_EQUAL -> return (left as Double) <= (right as Double)
            BANG_EQUAL -> return !isEqual(left, right)
            EQUAL_EQUAL -> return isEqual(left, right)
            MINUS -> return (left as Double) - (right as Double)
            PLUS -> {
                if (left is Double && right is Double) {
                    return left + right
                }
                if (left is String && right is String) {
                    return left + right
                }

                throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
            }
            SLASH -> return (left as Double) / (right as Double)
            STAR -> return (left as Double) * (right as Double)
            else -> {
                // Unreachable.
                return null
            }
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)

        if (expr.operator.type == OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        when (expr.operator.type) {
            BANG -> return isTruthy(right)
            MINUS -> {
                // TODO: checkNumberOperands
                return -(right as Double)
            }
            else -> {
                // Unreachable.
                return null
            }
        }
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        return lookUpVariable(expr.name, expr)
    }

    private fun lookUpVariable(name: Token, expr: Expr): Any? {
        return environment.get(name)
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        if (a == null) return false
        return a.equals(b)
    }

    private fun isTruthy(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj is Boolean) return obj
        return true
    }

    private fun stringify(obj: Any?): String {
        if (obj == null) return "nil"
        if (obj is Double) {
            var text = obj.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }

        return obj.toString()
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Void? {
        val value = evaluate(stmt.expression)
        println(stringify(value))
        return null
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Void? {
        evaluate(stmt.expression)
        return null
    }

    override fun visitVarStmt(stmt: Stmt.Var): Void? {
        val value: Any? = if (stmt.initializer != null) evaluate(stmt.initializer) else null
        environment.define(stmt.name.lexeme, value)
        return null
    }
}
