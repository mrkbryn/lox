package com.mab.jlox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.mab.jlox.TokenType.*;

class ScannerTest {
    List<Token> generateTokens(String source) {
        Scanner scanner = new Scanner(source);
        return scanner.scanTokens();
    }

    void scanAndAssertTokenTypes(String source, TokenType[] types) {
        List<Token> tokens = generateTokens(source);

        for (int i = 0; i < types.length && i < tokens.size(); i++) {
            TokenType expected = tokens.get(i).type;
            TokenType actual = types[i];
            assertEquals(expected, actual);
        }

        assertEquals(types.length, tokens.size());
    }

    void scanAndAssertTokens(String source, TokenType[] types, Object[] literals) {
        List<Token> tokens = generateTokens(source);

        // Validate TokenTypes
        for (int i = 0; i < types.length && i < tokens.size(); i++) {
            TokenType expected = tokens.get(i).type;
            TokenType actual = types[i];
            assertEquals(expected, actual);
        }
        assertEquals(types.length, tokens.size());

        // Validate literal values
        for (int i = 0; i < literals.length && i < tokens.size(); i++) {
            Object expected = tokens.get(i).literal;
            Object actual = literals[i];
            assertEquals(expected, actual);
        }
        assertEquals(literals.length, tokens.size());
    }

    @Test
    void testKeywords() {
        scanAndAssertTokenTypes("and", new TokenType[]{AND, EOF});
        scanAndAssertTokenTypes("class", new TokenType[]{CLASS, EOF});
        scanAndAssertTokenTypes("else", new TokenType[]{ELSE, EOF});
        scanAndAssertTokenTypes("false", new TokenType[]{FALSE, EOF});
        scanAndAssertTokenTypes("for", new TokenType[]{FOR, EOF});
        scanAndAssertTokenTypes("fun", new TokenType[]{FUN, EOF});
        scanAndAssertTokenTypes("if", new TokenType[]{IF, EOF});
        scanAndAssertTokenTypes("nil", new TokenType[]{NIL, EOF});
        scanAndAssertTokenTypes("or", new TokenType[]{OR, EOF});
        scanAndAssertTokenTypes("print", new TokenType[]{PRINT, EOF});
        scanAndAssertTokenTypes("return", new TokenType[]{RETURN, EOF});
        scanAndAssertTokenTypes("super", new TokenType[]{SUPER, EOF});
        scanAndAssertTokenTypes("this", new TokenType[]{THIS, EOF});
        scanAndAssertTokenTypes("true", new TokenType[]{TRUE, EOF});
        scanAndAssertTokenTypes("var", new TokenType[]{VAR, EOF});
        scanAndAssertTokenTypes("while", new TokenType[]{WHILE, EOF});
    }

    @Test
    void testIdentifier() {
        scanAndAssertTokenTypes("x", new TokenType[]{IDENTIFIER, EOF});
        scanAndAssertTokenTypes("var123", new TokenType[]{IDENTIFIER, EOF});
    }

    @Test
    void testSimpleChars() {
        scanAndAssertTokenTypes("( ) { } , . - + ; * ! != = == < <= > >=",
                new TokenType[]{
                        LEFT_PAREN, RIGHT_PAREN,
                        LEFT_BRACE, RIGHT_BRACE,
                        COMMA, DOT, MINUS, PLUS,
                        SEMICOLON, STAR, BANG, BANG_EQUAL,
                        EQUAL, EQUAL_EQUAL, LESS, LESS_EQUAL,
                        GREATER, GREATER_EQUAL,
                        EOF});
    }

    @Test
    void testString() {
        scanAndAssertTokens("\"Hello, World!\"",
                new TokenType[]{STRING, EOF},
                new Object[]{"Hello, World!", null});

        scanAndAssertTokens("\"123\"",
                new TokenType[]{STRING, EOF},
                new Object[]{"123", null});
    }

    @Test
    void testStringMultiline() {
        scanAndAssertTokens("\"Hello, World!\nHello, Again!\"",
                new TokenType[]{STRING, EOF},
                new Object[]{"Hello, World!\nHello, Again!", null});
    }

    @Test
    void testNumber() {
        scanAndAssertTokens("123", new TokenType[]{NUMBER, EOF}, new Object[]{123.0, null});
        scanAndAssertTokens("25.5", new TokenType[]{NUMBER, EOF}, new Object[]{25.5, null});
    }

    @Test
    void testWhitespace() {
        scanAndAssertTokenTypes("       5 + 5\n\n", new TokenType[]{NUMBER, PLUS, NUMBER, EOF});
    }

    @Test
    void testComments() {
        scanAndAssertTokenTypes("// Ignore comments... \n7", new TokenType[]{NUMBER, EOF});
    }
}