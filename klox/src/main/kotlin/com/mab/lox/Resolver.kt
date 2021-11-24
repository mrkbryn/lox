package com.mab.lox

import com.mab.lox.cli.Lox
import com.mab.lox.interpreter.Interpreter
import java.util.*
import kotlin.collections.HashMap

class Resolver(
    private val interpreter: Interpreter
) : Expr.Visitor<Void?>, Stmt.Visitor<Void?> {
    private val scopes: Stack<MutableMap<String, Boolean>> = Stack()
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

    private fun resolve(stmt: Stmt) = stmt.accept(this)

    private fun resolve(expr: Expr) = expr.accept(this)

    private fun beginScope() = scopes.push(HashMap())

    private fun endScope() = scopes.pop()

    private fun declare(name: Token) {
        if (scopes.isEmpty()) return

        val scope = scopes.peek()
        if (scope.containsKey(name.lexeme)) {
            Lox.loxRuntime.error(name, "Already a variable with this name is in scope.")
        }
        scope[name.lexeme] = false
    }

    private fun define(name: Token) {
        if (scopes.isEmpty()) return
        scopes.peek()[name.lexeme] = true
    }

    private fun resolveLocal(expr: Expr, name: Token) {
        for (i in scopes.size - 1 downTo 0) {
            if (scopes[i].containsKey(name.lexeme)) {
                interpreter.resolve(expr, scopes.size - 1 - i)
                return
            }
        }
    }

    private fun resolveFunction(function: Stmt.Function, type: FunctionType) {
        val enclosingFunction = currentFunction
        currentFunction = type

        beginScope()
        function.params.forEach { param ->
            declare(param)
            define(param)
        }
        resolve(function.body)
        endScope()
        currentFunction = enclosingFunction
    }

    //
    // Stmt.Visitor<Void> Interface Implementation.
    //

    override fun visitBlockStmt(stmt: Stmt.Block): Void? {
        beginScope()
        resolve(stmt.statements)
        endScope()
        return null
    }

    override fun visitClassStmt(stmt: Stmt.Class): Void? {
        val enclosingClass = currentClass
        currentClass = ClassType.CLASS

        declare(stmt.name)
        define(stmt.name)

        if (stmt.superclass != null && stmt.name.lexeme == stmt.superclass.name.lexeme) {
            Lox.loxRuntime.error(stmt.superclass.name, "A class can't inherit from itself.")
        }
        if (stmt.superclass != null) {
            currentClass = ClassType.SUBCLASS
            resolve(stmt.superclass)
        }

        if (stmt.superclass != null) {
            // If the class declaration has a superclass, then we create a new scope surrounding
            // all of its methods. In that scope, we define the name "super".
            beginScope()
            scopes.peek()["super"] = true
        }

        // Resolve "this" when we open up a class definition.
        beginScope()
        scopes.peek()["this"] = true

        stmt.methods.forEach { method ->
            var declaration = FunctionType.METHOD
            if (method.name.lexeme == "init") {
                declaration = FunctionType.INITIALIZER
            }
            resolveFunction(method, declaration)
        }

        endScope()

        if (stmt.superclass != null) {
            // Discard the class scope.
            endScope()
        }

        currentClass = enclosingClass
        return null
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Void? {
        resolve(stmt.expression)
        return null
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Void? {
        declare(stmt.name)

        // Unlike variables, we eagerly define the name of the function before resolving the function body
        // so that a function can recursively refer to itself inside the body.
        define(stmt.name)
        resolveFunction(stmt, FunctionType.FUNCTION)

        return null
    }

    override fun visitIfStmt(stmt: Stmt.If): Void? {
        resolve(stmt.condition)
        resolve(stmt.thenBranch)
        stmt.elseBranch?.let { resolve(it) }
        return null
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Void? {
        resolve(stmt.expression)
        return null
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Void? {
        if (currentFunction == FunctionType.NONE) {
            Lox.loxRuntime.error(stmt.keyword, "Can't return from top-level code.")
        }

        if (stmt.value != null) {
            if (currentFunction == FunctionType.INITIALIZER) {
                Lox.loxRuntime.error(stmt.keyword, "Can't return a value from an initializer.")
            }
            resolve(stmt.value)
        }

        return null
    }

    override fun visitVarStmt(stmt: Stmt.Var): Void? {
        declare(stmt.name)
        stmt.initializer?.let { resolve(it) }
        define(stmt.name)
        return null
    }

    override fun visitWhileStmt(stmt: Stmt.While): Void? {
        resolve(stmt.condition)
        resolve(stmt.body)
        return null
    }

    //
    // Expr.Visitor<Void> Interface Implementation.
    //

    override fun visitAssignExpr(expr: Expr.Assign): Void? {
        // First, resolve the expression for the assigned value in case it also
        // contains references to the other variables.
        resolve(expr.value)
        // Resolve to the variable that's being assigned to.
        resolveLocal(expr, expr.name)
        return null
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Void? {
        resolve(expr.left)
        resolve(expr.right)
        return null
    }

    override fun visitCallExpr(expr: Expr.Call): Void? {
        resolve(expr.callee)
        expr.arguments.forEach { resolve(it) }
        return null
    }

    override fun visitGetExpr(expr: Expr.Get): Void? {
        resolve(expr.obj)
        return null
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Void? {
        resolve(expr.expression)
        return null
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Void? {
        return null
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Void? {
        resolve(expr.left)
        resolve(expr.right)
        return null
    }

    override fun visitSetExpr(expr: Expr.Set): Void? {
        resolve(expr.value)
        resolve(expr.obj)
        return null
    }

    override fun visitSuperExpr(expr: Expr.Super): Void? {
        if (currentClass == ClassType.NONE) {
            Lox.loxRuntime.error(expr.keyword, "Can't use 'super' outside of a class.")
        } else if (currentClass != ClassType.SUBCLASS) {
            Lox.loxRuntime.error(expr.keyword, "Can't use 'super' in a class with no superclass.")
        }
        resolveLocal(expr, expr.keyword)
        return null
    }

    override fun visitThisExpr(expr: Expr.This): Void? {
        if (currentClass == ClassType.NONE) {
            Lox.loxRuntime.error(expr.keyword, "Can't use 'this' outside of a class.")
            return null
        }

        resolveLocal(expr, expr.keyword)
        return null
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Void? {
        resolve(expr.right)
        return null
    }

    override fun visitVariableExpr(expr: Expr.Variable): Void? {
        if (!scopes.isEmpty() && scopes.peek()[expr.name.lexeme] == false) {
            Lox.loxRuntime.error(expr.name, "Can't read local variable in its own initializer.")
        }

        resolveLocal(expr, expr.name)
        return null
    }
}
