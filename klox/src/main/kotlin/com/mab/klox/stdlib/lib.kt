package com.mab.klox.stdlib

import com.mab.klox.interpreter.LoxCallable
import com.mab.klox.interpreter.Interpreter
import java.lang.Exception
import kotlin.system.exitProcess

class LoxStandardLib {
    companion object {
        val globalFunctions = mapOf(
            "clock" to LoxClock(),
            "exit" to LoxExit(),
            "print" to LoxPrint(),
            "printErr" to LoxPrintErr(),
        )
    }
}

/**
 * Standard library function which returns the current time in nanoseconds.
 */
class LoxClock : LoxCallable {
    override fun arity(): Int = 0

    override fun call(interpreter: Interpreter?, arguments: List<Any?>): Any {
        return System.currentTimeMillis().toDouble() / 1000.0
    }

    override fun toString(): String = "<native fn>"
}

/**
 * Standard library function to exit program immediately with given status code.
 */
class LoxExit : LoxCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: Interpreter?, arguments: List<Any?>): Any? {
        val exitCode = arguments[0]
        if (exitCode is Int) exitProcess(exitCode)
        throw Exception("Bad input arg")
    }

    override fun toString(): String = "<native fn>"
}

/**
 * Standard library function to print to std out.
 */
class LoxPrint : LoxCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: Interpreter?, arguments: List<Any?>): Any? {
        println(arguments[0])
        return null
    }

    override fun toString(): String = "<native fn>"
}

/**
 * Standard library function to print to std err.
 */
class LoxPrintErr : LoxCallable {
    override fun arity(): Int = 1

    override fun call(interpreter: Interpreter?, arguments: List<Any?>): Any? {
        System.err.println(arguments[0])
        return null
    }

    override fun toString(): String = "<native fn>"
}
