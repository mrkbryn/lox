package com.mab.klox.interpreter

import com.mab.klox.error.RuntimeError
import com.mab.klox.scanner.Token

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
        if (method != null) {
            return method.bind(this)
        }

        // Should built-in functions be wired in at creation time? Or is it better to have this lookup
        // functionality built into the LoxInstance class.
        // TODO: add parser support to allow CLASS keyword after DOT as property
        if (name.lexeme == "klass") {
            return klass
        }

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
