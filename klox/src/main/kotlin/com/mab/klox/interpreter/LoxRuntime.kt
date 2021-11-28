package com.mab.klox.interpreter

import com.mab.klox.*
import com.mab.klox.error.RuntimeError
import com.mab.klox.scanner.Scanner
import com.mab.klox.scanner.Token
import com.mab.klox.scanner.TokenType
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class LoxRuntime {
    private var hadError = false
    private var hadRuntimeError = false;
    private val interpreter = Interpreter()

    fun runFile(path: String) {
        val bytes = Files.readAllBytes(Paths.get(path))
        run(String(bytes, Charset.defaultCharset()))

        // Indicate an error in the exit code.
        if (hadError) exitProcess(65)
        if (hadRuntimeError) exitProcess(70)
    }

    fun runPrompt() {
        val input = InputStreamReader(System.`in`)
        val reader = BufferedReader(input)

        while (true) {
            print("> ")
            val line = reader.readLine()
            line?.let { run(line) }
            hadError = false
        }
    }

    fun run(source: String) {
        val tokens = Scanner(source)
            .scanTokens()
        val statements = Parser(tokens)
            .parse()

        // Stop if there was a syntax error.
        if (hadError) return

        val resolver = Resolver(interpreter)
        resolver.resolve(statements)

        // Stop if there was a resolution error.
        if (hadError) return

        interpreter.interpret(statements)
    }

    private fun report(line: Int, where: String, message: String) {
        System.err.println("[line $line] Error$where: $message")
        hadError = true
    }

    fun error(line: Int, message: String) = report(line, "", message)

    fun error(token: Token, message: String) {
        when (token.type) {
            TokenType.EOF -> report(token.line, " at end", message)
            else -> report(token.line, " at '${token.lexeme}'", message)
        }
    }

    fun runtimeError(error: RuntimeError) {
        System.err.println("${error.message} \n[line ${error.token.line}]")
        hadRuntimeError = true
    }
}
