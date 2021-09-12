package com.mab.lox

class LoxFunction(val declaration: Stmt.Function, val closure: Environment, val isInitializer: Boolean) : LoxCallable {
    /**
     * Returns the number of arguments expected in a function call.
     */
    override fun arity(): Int = declaration.params.size

    /**
     * Executes the function with the given arguments within the passed Interpreter.
     */
    override fun call(interpreter: Interpreter?, arguments: List<Any?>?): Any? {
        val environment = Environment(closure)
        declaration.params.forEachIndexed { index, param ->
            if (arguments != null) {
                environment.define(param.lexeme, arguments.get(index))
            }
        }

        // TODO

        return null
    }

    override fun toString(): String {
        return "<fn ${declaration.name.lexeme}>"
    }

    fun bind(instance: LoxInstance): LoxFunction {
        val environment = Environment(closure)
        environment.define("this", instance)
        return LoxFunction(declaration, environment, isInitializer)
    }
}