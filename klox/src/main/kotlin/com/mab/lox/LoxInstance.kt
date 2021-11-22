package com.mab.lox

/**
 * Represents an instance of a LoxClass. LoxClass instances are simply a collection of properties to class properties
 * or class methods.
 */
class LoxInstance(
    private val klass: LoxClass,
    private val fields: MutableMap<String, Any?> = mutableMapOf()
) {
    /**
     * Returns either the property or class method for the given token.
     */
    fun get(name: Token): Any? {
        if (fields.containsKey(name.lexeme)) {
            return fields[name.lexeme]
        }

        val method = klass.findMethod(name.lexeme)
        if (method != null) return method.bind(this)
        throw RuntimeError(
            token = name,
            message = "Undefined property '${name.lexeme}'."
        )
    }

    /**
     * Sets a named property on this instance.
     */
    fun set(name: Token, value: Any?) {
        fields[name.lexeme] = value
    }

    override fun toString(): String {
        return "${klass.name} instance"
    }
}
