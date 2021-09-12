package com.mab.lox

class LoxStandardLib {
    companion object {
        val global_functions = mapOf(
            "clock" to LoxClock(),
            "exit" to LoxExit(),
            "print" to LoxPrint(),
            "print_err" to LoxPrintErr(),
        )
    }
}

/**
 * Standard library function which returns the current time in nanoseconds.
 */
class LoxClock : LoxCallable {
    override fun arity(): Int = 0

    override fun call(interpreter: Interpreter?, arguments: List<Any?>?): Any {
        return System.currentTimeMillis().toDouble() / 1000.0
    };
}

/**
 * Standard library function to exit program immediately with given status code.
 */
class LoxExit : LoxCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: Interpreter?, arguments: List<Any?>?): Any? {
        System.exit(arguments?.get(0) as Int)
        return null
    }
}

/**
 * Standard library function to print to std out.
 */
class LoxPrint : LoxCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: Interpreter?, arguments: List<Any?>?): Any? {
        println(arguments?.get(0))
        return null
    }
}

/**
 * Standard library function to print to std err.
 */
class LoxPrintErr : LoxCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: Interpreter?, arguments: List<Any?>?): Any? {
        System.err.println(arguments?.get(0))
        return null
    }
}
