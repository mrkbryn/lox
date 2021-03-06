package com.mab.klox.cli

import com.mab.klox.interpreter.LoxRuntime

class Lox {
    companion object {
        var loxRuntime = LoxRuntime()

        fun resetRuntime() {
            loxRuntime = LoxRuntime()
        }
    }
}

fun main(args: Array<String>) {
    when {
        args.size > 1 -> println("Usage klox [script]")
        args.size == 1 -> Lox.loxRuntime.runFile(args[0])
        else -> Lox.loxRuntime.runPrompt()
    }
}
