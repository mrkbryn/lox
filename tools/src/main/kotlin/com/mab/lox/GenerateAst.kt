package com.mab.lox

import java.io.PrintWriter

class GenerateAst {
    companion object {
        const val basePackage = "com.mab.klox"
    }
}

const val baseDir = "/Users/mabryan/code/lox"
const val kloxOutputDir = "$baseDir/klox/src/main/kotlin/com/mab/klox"
const val jloxOutputDir = "$baseDir/jlox/src/main/java/com/mab/jlox"

fun main() {
    defineAst(
        outputDir = kloxOutputDir,
        baseName = "Expr",
        types = listOf(
            assignExpr,
            binaryExpr,
            callExpr,
            getExpr,
            groupingExpr,
            literalExpr,
            logicalExpr,
            setExpr,
            superExpr,
            thisExpr,
            variableExpr,
            unaryExpr,
        )
    )

    defineAst(
        outputDir = kloxOutputDir,
        baseName = "Stmt",
        types = listOf(
            blockStmt,
            classStmt,
            expressionStmt,
            functionStmt,
            ifStmt,
            varStmt,
            printStmt,
            returnStmt,
            whileStmt,
        )
    )
}

fun defineAst(
    outputDir: String,
    baseName: String,
    types: List<AstClass>
) {
    val path = "$outputDir/${baseName}.kt"
    val writer = PrintWriter(path, "UTF-8")

    writer.println("package ${GenerateAst.basePackage}")
    writer.println()
    writer.println("import com.mab.klox.scanner.Token")
    writer.println()
    writer.println("abstract class $baseName {")

    defineVisitor(writer, baseName, types)

    types.forEach {
        defineType(writer, baseName, it.className, it.args)
    }

    writer.println("    abstract fun <R> accept(visitor: Visitor<R>): R")
    writer.println("}")
    writer.close()
}

fun defineVisitor(
    writer: PrintWriter,
    baseName: String,
    types: List<AstClass>
) {
    writer.println("    interface Visitor<R> {")
    types.forEach {
        val typeName = it.className
        writer.println("        fun visit$typeName$baseName(${baseName.toLowerCase()}: $typeName): R")
    }
    writer.println("    }")
    writer.println()
}

fun defineType(
    writer: PrintWriter,
    baseName: String,
    className: String,
    fieldList: List<Field>
) {
    writer.println("    class $className(${fieldList.joinToString { it.formatKotlin() }}) : $baseName() {")
    writer.println("        override fun <R> accept(visitor: Visitor<R>): R {")
    writer.println("            return visitor.visit$className$baseName(this)")
    writer.println("        }")
    writer.println("    }")
    writer.println()
}
