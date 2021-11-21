import com.mab.lox.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AstPrinterTest {
    @Test
    fun testBinaryExpr() {
        val expression = Expr.Binary(
            left = Expr.Literal(100),
            operator = Token(
                type = TokenType.PLUS,
                lexeme = "+",
                literal = null,
                line = 1
            ),
            right = Expr.Literal(200)
        )
        assertEquals(
            "(+ 100 200)",
            AstPrinter().print(expression)
        )
    }

    @Test
    fun testLiteralExpr() {
        val expression1 = Expr.Literal("foobar")
        assertEquals("foobar", AstPrinter().print(expression1))
        val expression2 = Expr.Literal(200)
        assertEquals("200", AstPrinter().print(expression2))
    }

    @Test
    fun testGroupingExpr() {
        val expression =
            Expr.Grouping(
                Expr.Grouping(
                    Expr.Grouping(
                        Expr.Literal("x")
                    )
                )
            )
        assertEquals(
            "(group (group (group x)))",
            AstPrinter().print(expression)
        )
    }

    @Test
    fun testLogicalExpr() {
        val expression = Expr.Logical(
            Expr.Literal(true),
            Token(TokenType.AND, "and", null, 1),
            Expr.Binary(
                left = Expr.Literal(10),
                operator = Token(
                    type = TokenType.LESS_EQUAL,
                    lexeme = "<=",
                    literal = null,
                    line = 1
                ),
                Expr.Literal(15)
            )
        )
        assertEquals("(and true (<= 10 15))", AstPrinter().print(expression))
    }

    @Test
    fun testExpr() {
        val expression =
            Expr.Binary(
                left = Expr.Unary(
                    Token(
                        type = TokenType.MINUS,
                        lexeme = "-",
                        literal = null,
                        line = 1
                    ),
                    Expr.Literal(value = 123)
                ),
                operator = Token(
                    type = TokenType.STAR,
                    lexeme = "*",
                    literal = null,
                    line = 1
                ),
                right = Expr.Grouping(
                    Expr.Literal(45.67)
                )
            )

        assertEquals(
            "(* (- 123) (group 45.67))",
            AstPrinter().print(expression)
        )

        val statement = Stmt.Print(expression)
        assertEquals(
            "(print (* (- 123) (group 45.67)))",
            AstPrinter().print(statement)
        )
    }

    @Test
    fun testAssignExpr() {
        val expression = Expr.Assign(
            name = Token(
                type = TokenType.IDENTIFIER,
                lexeme = "x",
                literal = "x",
                line = 1
            ),
            value = Expr.Literal(20)
        )
        // TODO: why do we have an extra space here?
        assertEquals("(x :=  20)", AstPrinter().print(expression))
    }

    @Test
    fun testUnaryExpr() {
        val expression =
            Expr.Unary(
                operator = Token(
                    type = TokenType.MINUS,
                    lexeme = "-",
                    literal = null,
                    line = 1
                ),
                right = Expr.Literal(102.5)
            )
        assertEquals(
            "(- 102.5)",
            AstPrinter().print(expression)
        )
    }
}
