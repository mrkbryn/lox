package com.mab.klox.interpreter

import com.mab.klox.interpreter.Interpreter

/**
 * Interface representing a class method or function that can be called.
 */
interface LoxCallable {
    /**
     * Returns the number of arguments defined for the function.
     */
    fun arity(): Int

    /**
     * Executes the function with the given arguments lists.
     */
    fun call(interpreter: Interpreter?, arguments: List<Any?>): Any?
}
