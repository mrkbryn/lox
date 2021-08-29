package com.mab.lox

import com.mab.lox.TokenType.*

class Interpreter : Expr.Visitor<Any?> {

    fun interpret(expression: Expr): Any? {
        try {
            val value = evaluate(expression)
            println(stringify(value))
            return value
        } catch (error: RuntimeError) {
            println("Runtime error!")
        }
        return null
    }

    private fun evaluate(expr: Expr) = expr.accept(this)

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

}
