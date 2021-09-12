package com.mab.lox

/**
 * Represents an instance of a LoxClass. LoxClass instances are simply a collection of properties to class properties
 * or class methods.
 */
class LoxInstance(val klass: LoxClass, val fields: HashMap<String, Any?> = HashMap()) {
    /**
     * Returns either the property or class method for the given token.
     */
    fun get(name: Token): Any? {
        if (fields.containsKey(name.lexeme)) {
            return fields[name.lexeme]
        }

        val method = klass.findMethod(name.lexeme)
        if (method != null) return method.bind(this)
        throw RuntimeError(name, "Undefined property '${name.lexeme}'.")
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