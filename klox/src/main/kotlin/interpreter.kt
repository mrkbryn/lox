package com.mab.lox

import com.mab.lox.TokenType.*

class Interpreter(var environment: Environment = Environment()) : Expr.Visitor<Any?>, Stmt.Visitor<Void?> {
    private val globals: Environment = Environment()
    private val locals: HashMap<Expr, Int> = HashMap()

    init {
        // Populate global functions from Lox's standard library.
        LoxStandardLib.global_functions.forEach { name, function -> globals.define(name, function) }
    }

    fun interpret(statements: List<Stmt>) {
        try {
            statements.forEach { execute(it) }
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    private fun lookUpVariable(name: Token, expr: Expr): Any? {
        val distance = locals[expr]
        return if (distance != null) {
            environment.getAt(distance, name.lexeme)
        } else {
            globals.get(name)
        }
    }

    private fun evaluate(expr: Expr) = expr.accept(this)

    private fun execute(stmt: Stmt) = stmt.accept(this)

    fun resolve(expr: Expr, depth: Int) {
        locals[expr] = depth
    }

    private fun executeBlock(statements: List<Stmt>, environment: Environment) {
        val previous = this.environment
        try {
            this.environment = environment
            statements.forEach { execute(it) }
        } finally {
            this.environment = previous
        }
    }

    private fun checkNumberOperands(operator: Token, vararg operands: Any?) {
        operands.forEach {
            if (it !is Double) {
                throw RuntimeError(operator, "Operand(s) must be numbers.")
            }
        }
    }

    //
    // Expr.Visitor Interface Implementation.
    //

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)
        // TODO: implement locals
        environment.assign(expr.name, value)
        return value
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        when (expr.operator.type) {
            GREATER -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) > (right as Double)
            }
            GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) >= (right as Double)
            }
            LESS -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) < (right as Double)
            }
            LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) <= (right as Double)
            }
            BANG_EQUAL -> return !isEqual(left, right)
            EQUAL_EQUAL -> return isEqual(left, right)
            MINUS -> {
                checkNumberOperands(expr.operator, right)
                return (left as Double) - (right as Double)
            }
            PLUS -> {
                if (left is Double && right is Double) {
                    return left + right
                }
                if (left is String && right is String) {
                    return left + right
                }

                throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
            }
            SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) / (right as Double)
            }
            STAR -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) * (right as Double)
            }
            else -> {
                // Unreachable.
                return null
            }
        }
    }

    override fun visitCallExpr(expr: Expr.Call): Any? {
        val callee = evaluate(expr.callee)
        val arguments = ArrayList<Any?>()
        expr.arguments.forEach {
            arguments.add(evaluate(it))
        }

        if (callee !is LoxCallable) {
            throw RuntimeError(expr.paren, "Can only call functions and classes.")
        }

        if (arguments.size != callee.arity()) {
            throw RuntimeError(expr.paren, "Expected ${callee.arity()} arguments but got ${arguments.size}.")
        }
        return callee.call(this, arguments)
    }

    override fun visitGetExpr(expr: Expr.Get): Any? {
        val obj = evaluate(expr.obj)
        if (obj is LoxInstance) return obj.get(expr.name)
        throw RuntimeError(expr.name, "Only instances have properties.")
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? = evaluate(expr.expression)

    override fun visitLiteralExpr(expr: Expr.Literal): Any? = expr.value

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)

        if (expr.operator.type == OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }

        return evaluate(expr.right)
    }

    override fun visitSetExpr(expr: Expr.Set): Any? {
        val obj = evaluate(expr.obj)
        if (obj !is LoxInstance) {
            throw RuntimeError(expr.name, "Only instances have fields.")
        }

        val value = evaluate(expr.value)
        obj.set(expr.name, value)
        return value
    }

    override fun visitSuperExpr(expr: Expr.Super): Any? {
        val distance = locals[expr]!!
        val superclass = environment.getAt(distance, "super") as LoxClass
        val obj = environment.getAt(distance - 1, "this") as LoxInstance
        val method = superclass.findMethod(expr.method.lexeme)
            ?: throw RuntimeError(expr.method, "Undefined property '${expr.method.lexeme}'.")
        return method.bind(obj)
    }

    override fun visitThisExpr(expr: Expr.This): Any? = lookUpVariable(expr.keyword, expr)

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        when (expr.operator.type) {
            BANG -> return isTruthy(right)
            MINUS -> {
                checkNumberOperands(expr.operator, right)
                return -(right as Double)
            }
            else -> {
                // Unreachable.
                return null
            }
        }
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? = lookUpVariable(expr.name, expr)

    //
    // Stmt.Visitor Interface Implementation.
    //

    override fun visitBlockStmt(stmt: Stmt.Block): Void? {
        executeBlock(stmt.statements, Environment(environment))
        return null
    }

    override fun visitClassStmt(stmt: Stmt.Class): Void? {
        TODO("Not yet implemented")
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Void? {
        evaluate(stmt.expression)
        return null
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Void? {
        TODO("Not yet implemented")
    }

    override fun visitIfStmt(stmt: Stmt.If): Void? {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }
        return null
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Void? {
        val value = evaluate(stmt.expression)
        println(stringify(value))
        return null
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Void? {
        TODO("Not yet implemented")
    }

    override fun visitVarStmt(stmt: Stmt.Var): Void? {
        val value: Any? = if (stmt.initializer != null) evaluate(stmt.initializer) else null
        environment.define(stmt.name.lexeme, value)
        return null
    }

    override fun visitWhileStmt(stmt: Stmt.While): Void? {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }
        return null
    }
}
