package com.mab.lox

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
    fun call(interpreter: Interpreter?, arguments: List<Any?>?): Any?
}
