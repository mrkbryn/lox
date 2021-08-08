package lox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
    static final Interpreter interpreter = new Interpreter();

    Expr generateExpr(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        return parser.parse();
    }

    Object eval(String source) {
        Expr expression = generateExpr(source);
        return interpreter.interpret(expression);
    }

    @Test
    void interpretMath() {
        assertEquals(6.0, eval("1 + 5"));
        assertEquals(31.0, eval("1 + 5 * 6"));
        assertEquals(31.0, eval("5 * 6 + 1"));
        assertEquals(60.0, eval("(5 + 1) * 10"));
        assertEquals(-1.0, eval("-1.0"));
        assertEquals(-5.0, eval("-1.0 * 5.0"));
    }

    @Test
    void interpretString() {
        // Simple string.
        assertEquals("Hello", eval("\"Hello\""));

        // String concatenation.
        assertEquals("Hello, World!", eval("\"Hello\" + \", World!\""));
    }

    @Test
    void testTruthy() {
        // true | false
        assertEquals(true, eval("true"));
        assertEquals(false, eval("false"));

        // !
        assertEquals(true, eval("!false"));
        assertEquals(false, eval("!true"));

        // >
        assertEquals(true, eval("5 > 1"));
        assertEquals(false, eval("1 > 5"));

        // >=
        assertEquals(true, eval("5 >= 1"));
        assertEquals(false, eval("1 >= 5"));

        // <
        assertEquals(true, eval("5 < 10"));
        assertEquals(false, eval("10 < 5"));

        // <=
        assertEquals(true, eval("5 <= 10"));
        assertEquals(false, eval("10 <= 5"));

        // !=
        assertEquals(true, eval("5 != 10"));
        assertEquals(false, eval("5 != 5"));
        assertEquals(true, eval("\"hello\" != \"hellox\""));
        assertEquals(false, eval("\"hello\" != \"hello\""));

        // ==
        assertEquals(true, eval("5 == 5"));
        assertEquals(false, eval("5 == 10"));
        assertEquals(true, eval("\"hello\" == \"hello\""));
        assertEquals(false, eval("\"hello\" == \"hellox\""));
    }

    @Test
    void testEquality() {
        assertEquals(true, eval("nil == nil"));
        assertEquals(false, eval("nil != nil"));
        assertEquals(false, eval("5.0 == nil"));
        assertEquals(true, eval("5.0 == 5.0"));
        assertEquals(false, eval("5.0 == \"hello\""));
    }
}