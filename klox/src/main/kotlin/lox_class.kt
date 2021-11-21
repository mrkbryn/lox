package com.mab.lox

/**
 * Representation of a class definition for the Lox language. The Lox class provides a default `init` class method
 * which can be overridden. Calling a LoxClass returns a new instance of the class.
 */
class LoxClass(
    val name: String,
    private val superclass: LoxClass?,
    private val methods: Map<String, LoxFunction>
) : LoxCallable {
    override fun arity(): Int {
        val initializer = findMethod("init") ?: return 0
        return initializer.arity()
    }

    override fun call(interpreter: Interpreter?, arguments: List<Any?>?): Any? {
        val instance = LoxInstance(this)
        val initializer = findMethod("init")
        initializer?.bind(instance)?.call(interpreter, arguments)
        return instance
    }

    fun findMethod(name: String): LoxFunction? {
        if (methods.containsKey(name)) {
            return methods[name]
        }
        if (superclass != null) {
            return superclass.findMethod(name)
        }
        return null
    }

    override fun toString(): String {
        return name
    }
}
