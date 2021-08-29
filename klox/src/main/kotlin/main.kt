package com.mab.lox

class Lox {
    companion object {
        var hadError = false
        var hadRuntimeError = false;
        val interpreter = Interpreter()

        fun runFile(path: String) {
            // TODO
        }

        fun runPrompt() {
            while (true) {
                print("> ")
                val line = readLine()
                line?.let { run(line) }
                hadError = false
            }
        }

        private fun run(source: String) {
            val scanner = Scanner(source)
            val tokens = scanner.scanTokens()

            val parser = Parser(tokens)
            val statements = parser.parse()

            // Stop if there was a syntax error.
            if (hadError) return

            interpreter.interpret(statements)
        }

        fun report(line: Int, where: String, message: String) {
            System.err.println("[line $line] Error$where: $message")
            hadError = true
        }

        fun error(line: Int, message: String) = report(line, "", message)

        fun error(token: Token, message: String) {
            if (token.type == TokenType.EOF) {
                report(token.line, " at end", message)
            } else {
                report(token.line, " at '${token.lexeme}'", message)
            }
        }
    }
}

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage klox [script]")
    } else if (args.size == 1) {
        Lox.runFile(args[0])
    } else {
        Lox.runPrompt()
    }
}
