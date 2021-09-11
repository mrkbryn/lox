package com.mab.lox

import java.io.PrintWriter
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    println("Running generate_ast with args: ${args}")
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>")
        exitProcess(64)
    }

    val outputDir = args[0]

    defineAst(outputDir, "Expr", listOf(
        "Assign     | val name: Token, val value: Expr",
        "Binary     | val left: Expr, val operator: Token, val right: Expr",
//        "Call       | val callee: Expr, val paren: Token, val arguments: List<Expr>",
//        "Get        | val obj: Expr, val name: Token",
        "Grouping   | val expression: Expr",
        "Literal    | val value: Any?",
        "Logical    | val left: Expr, val operator: Token, val right: Expr",
//        "Set        | val obj: Expr, val name: Token, val value: Expr",
//        "Super      | val keyword: Token, val method: Token",
//        "This       | val keyword: Token",
        "Variable   | val name: Token",
        "Unary      | val operator: Token, val right: Expr",
    ))

    defineAst(outputDir, "Stmt", listOf(
        "Block      | val statements: List<Stmt>",
//        "Class      | val name: Token, val superclass: Expr.Variable?, val methods: List<Stmt.Function>",
        "Expression | val expression: Expr",
//        "Function   | val name: Token, val params: List<Token>, val body: List<Stmt>",
        "If         | val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?",
        "Var        | val name: Token, val initializer: Expr?",
        "Print      | val expression: Expr",
//        "Return     | val keyword: Token, val value: Expr?",
        "While      | val condition: Expr, val body: Stmt",
    ))
}

fun defineAst(outputDir: String, baseName: String, types: List<String>) {
    val path = "$outputDir/${baseName.toLowerCase()}.kt"
    val writer = PrintWriter(path, "UTF-8")

    writer.println("package com.mab.lox")
    writer.println()
    writer.println("abstract class $baseName {")

    defineVisitor(writer, baseName, types)

    types.forEach {
        // E.g., "Binary | left: Expr, operator: Token operator, right: Expr"
        val className = it.split("|")[0].trim()
        val fields = it.split("|")[1].trim()
        defineType(writer, baseName, className, fields)
    }

    writer.println("    abstract fun <R> accept(visitor: Visitor<R>): R")
    writer.println("}")
    writer.close()
}

fun defineVisitor(writer: PrintWriter, baseName: String, types: List<String>) {
    writer.println("    interface Visitor<R> {")
    types.forEach {
        val typeName = it.split("|")[0].trim()
        writer.println("        fun visit$typeName$baseName(${baseName.toLowerCase()}: $typeName): R")
    }
    writer.println("    }")
    writer.println()
}

fun defineType(writer: PrintWriter, baseName: String, className: String, fieldList: String) {
    writer.println("    class $className($fieldList) : $baseName() {")
    writer.println("        override fun <R> accept(visitor: Visitor<R>): R {")
    writer.println("            return visitor.visit$className$baseName(this)")
    writer.println("        }")
    writer.println("    }")
    writer.println()
}