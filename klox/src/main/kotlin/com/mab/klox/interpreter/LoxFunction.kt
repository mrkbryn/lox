package com.mab.klox.interpreter

import com.mab.klox.Stmt

class LoxFunction(
    private val declaration: Stmt.Function,
    private val closure: Environment,
    private val isInitializer: Boolean
) : LoxCallable {
    /**
     * Returns the number of arguments expected in a function call.
     */
    override fun arity(): Int = declaration.params.size

    /**
     * Executes the function with the given arguments within the passed Interpreter.
     */
    override fun call(interpreter: Interpreter?, arguments: List<Any?>): Any? {
        val environment = Environment(closure)
        declaration.params.zip(arguments) { param, arg ->
            environment.define(
                name = param.lexeme,
                value = arg
            )
        }

        try {
            interpreter!!.executeBlock(declaration.body, environment)
        } catch (returnValue: Return) {
            if (isInitializer) {
                return closure.getAt(0, "this");
            }
            return returnValue.value
        }

        if (isInitializer) return closure.getAt(0, "this")
        return null
    }

    override fun toString(): String {
        return "<fn ${declaration.name.lexeme}>"
    }

    fun bind(instance: LoxInstance): LoxFunction {
        val environment = Environment(closure)
        environment.define("this", instance)
        return LoxFunction(
            declaration = declaration,
            closure = environment,
            isInitializer = isInitializer
        )
    }
}
