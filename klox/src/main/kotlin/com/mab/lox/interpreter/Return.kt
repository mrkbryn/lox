package com.mab.lox.interpreter

/**
 * Represents a return value from a function. Used to unwind the stack within a function call.
 */
class Return(val value: Any?) : RuntimeException(null, null, false, false)
