import com.mab.lox.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AstPrinterTest {
    @Test
    fun testBinaryExpr() {
        val expression = Expr.Binary(
            Expr.Literal(100),
            Token(TokenType.PLUS, "+", null, 1),
            Expr.Literal(200)
        )
        assertEquals("(+ 100 200)", AstPrinter().print(expression))
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
        val expression = Expr.Grouping(Expr.Grouping(Expr.Grouping(Expr.Literal("x"))))
        assertEquals("(group (group (group x)))", AstPrinter().print(expression))
    }

    @Test
    fun testLogicalExpr() {
        val expression = Expr.Logical(
            Expr.Literal(true),
            Token(TokenType.AND, "and", null, 1),
            Expr.Binary(
                Expr.Literal(10),
                Token(TokenType.LESS_EQUAL, "<=", null, 1),
                Expr.Literal(15)
            )
        )
        assertEquals("(and true (<= 10 15))", AstPrinter().print(expression))
    }

    @Test
    fun testExpr() {
        val expression = Expr.Binary(
            Expr.Unary(
                Token(TokenType.MINUS, "-", null, 1),
                Expr.Literal(123)),
            Token(TokenType.STAR, "*", null, 1),
            Expr.Grouping(Expr.Literal(45.67)))

        assertEquals("(* (- 123) (group 45.67))", AstPrinter().print(expression))

        val statement = Stmt.Print(expression)
        assertEquals("(print (* (- 123) (group 45.67)))", AstPrinter().print(statement))
    }

    @Test
    fun testAssignExpr() {
        val expression = Expr.Assign(
            Token(TokenType.IDENTIFIER, "x", "x", 1),
            Expr.Literal(20)
        )
        // TODO: why do we have an extra space here?
        assertEquals("(x :=  20)", AstPrinter().print(expression))
    }
}
