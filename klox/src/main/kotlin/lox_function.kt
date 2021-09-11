package com.mab.lox

class LoxFunction(val declaration: Stmt.Function, val closure: Environment, val isInitializer: Boolean) : LoxCallable {
    override fun arity(): Int {
        return declaration.params.size
    }

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