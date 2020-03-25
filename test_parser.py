import unittest
from parser import Parser
from tokens import Token, TokenType
from expressions import Expression, LiteralExpr, BinaryExpr


class TestParser(unittest.TestCase):
    def test_literal_expression(self):
        # 5;
        tokens = [
            Token(TokenType.NUMBER, "5", 5.0, 0),
            Token(TokenType.SEMICOLON, ";", None, 0),
            Token(TokenType.EOF, "", None, 0),
        ]
        parser = Parser(tokens)
        statements = parser.parse()
        self.assertEqual(len(statements), 1, "Expected 1 statement")
        expression_statement = statements[0]
        self.assertTrue(isinstance(expression_statement, Expression))
        literal_expression = expression_statement.expression
        self.assertTrue(isinstance(literal_expression, LiteralExpr))
        self.assertEqual(literal_expression.value, 5)

    def test_binary_expression(self):
        # 10 <= 20;
        tokens = [
            Token(TokenType.NUMBER, "10", 10.0, 0),
            Token(TokenType.LESS_EQUAL, "<=", None, 0),
            Token(TokenType.NUMBER, "20", 20.0, 0),
            Token(TokenType.SEMICOLON, ";", None, 0),
            Token(TokenType.EOF, "", None, 0),
        ]
        parser = Parser(tokens)
        statements = parser.parse()
        self.assertEqual(len(statements), 1, "Expected 1 statement")
        expression = statements[0].expression
        self.assertTrue(isinstance(expression, BinaryExpr))
        self.assertEqual(expression.left.value, 10)
        self.assertEqual(expression.operator.token_type, TokenType.LESS_EQUAL)
        self.assertEqual(expression.right.value, 20)
