package com.mab.lox

data class Field(
    val name: String,
    val type: String
)

fun Field.formatKotlin() = "val $name: $type"

data class AstClass(
    val className: String,
    val args: List<Field>
)

// Exprs
val assignExpr = AstClass("Assign", listOf(Field("name", "Token"), Field("value", "Expr")))
val binaryExpr = AstClass("Binary", listOf(Field("left", "Expr"), Field("operator", "Token"), Field("right", "Expr")))
val callExpr = AstClass("Call", listOf(Field("callee", "Expr"), Field("paren", "Token"), Field("arguments", "List<Expr>")))
val getExpr = AstClass("Get", listOf(Field("obj", "Expr"), Field("name", "Token")))
val groupingExpr = AstClass("Grouping", listOf(Field("expression", "Expr")))
val literalExpr = AstClass("Literal", listOf(Field("value", "Any?")))
val logicalExpr = AstClass("Logical", listOf(Field("left", "Expr"), Field("operator", "Token"), Field("right", "Expr")))
val setExpr = AstClass("Set", listOf(Field("obj", "Expr"), Field("name", "Token"), Field("value", "Expr")))
val superExpr = AstClass("Super", listOf(Field("keyword", "Token"), Field("method", "Token")))
val thisExpr = AstClass("This", listOf(Field("keyword", "Token")))
val variableExpr = AstClass("Variable", listOf(Field("name", "Token")))
val unaryExpr = AstClass("Unary", listOf(Field("operator", "Token"), Field("right", "Expr")))

// Stmts
val blockStmt = AstClass("Block", listOf(Field("statements", "List<Stmt>")))
val classStmt = AstClass("Class", listOf(Field("name", "Token"), Field("superclass", "Expr.Variable?"), Field("methods", "List<Stmt.Function>")))
val expressionStmt = AstClass("Expression", listOf(Field("expression", "Expr")))
val functionStmt = AstClass("Function", listOf(Field("name", "Token"), Field("params", "List<Token>"), Field("body", "List<Stmt>")))
val ifStmt = AstClass("If", listOf(Field("condition", "Expr"), Field("thenBranch", "Stmt"), Field("elseBranch", "Stmt?")))
val varStmt = AstClass("Var", listOf(Field("name", "Token"), Field("initializer", "Expr?")))
val printStmt = AstClass("Print", listOf(Field("expression", "Expr")))
val returnStmt = AstClass("Return", listOf(Field("keyword", "Token"), Field("value", "Expr?")))
val whileStmt = AstClass("While", listOf(Field("condition", "Expr"), Field("body", "Stmt")))
