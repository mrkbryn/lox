import com.mab.lox.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class AstPrinterTest {
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
