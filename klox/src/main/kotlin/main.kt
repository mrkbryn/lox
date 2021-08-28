package com.mab.lox

var hadError = false;
var hadRuntimeError = false;

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

fun run(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()
    val parser = Parser(tokens)
    val expressions = parser.parse()
    print(expressions)
}

fun report(line: Int, where: String, message: String) {
    System.err.println("[line $line] Error$where: $message")
    hadError = true
}

fun error(line: Int, message: String) = report(line, "", message)

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage klox [script]")
    } else if (args.size == 1) {
        runFile(args[0])
    } else {
        runPrompt()
    }
}
