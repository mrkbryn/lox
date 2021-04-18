import unittest
from pylox.tokens import TokenType
from pylox.scanner import Scanner


class TestScanner(unittest.TestCase):

    def assertTokens(self, tokens, expected):
        for t in range(len(expected)):
            token_type, lexeme, literal = expected[t]
            token = tokens[t]
            self.assertEqual(token.token_type, token_type, "Incorrect token type")
            self.assertEqual(token.lexeme, lexeme, "Incorrect token lexeme")
            self.assertEqual(token.literal, literal, "Incorrect token literal")

    def test_keywords(self):
        scanner = Scanner("5")
        tokens = scanner.scan_tokens()
        expected = [
            (TokenType.NUMBER, "5", 5),
            (TokenType.EOF, "", None),
        ]
        self.assertTokens(tokens, expected)

    def test_inequalities(self):
        scanner = Scanner("10 <= 20")
        tokens = scanner.scan_tokens()
        expected = [
            (TokenType.NUMBER, "10", 10),
            (TokenType.LESS_EQUAL, "<=", None),
            (TokenType.NUMBER, "20", 20),
            (TokenType.EOF, "", None),
        ]
        self.assertTokens(tokens, expected)

        scanner = Scanner("1 >= 2")
        tokens = scanner.scan_tokens()
        expected = [
            (TokenType.NUMBER, "1", 1),
            (TokenType.GREATER_EQUAL, ">=", None),
            (TokenType.NUMBER, "2", 2),
            (TokenType.EOF, "", None),
        ]
        self.assertTokens(tokens, expected)

    def test_numbers(self):
        # parse integer
        scanner = Scanner("5")
        tokens = scanner.scan_tokens()
        self.assertEqual(tokens[0].literal, 5.0)
        
        # parse floats
        scanner = Scanner("5.105")
        tokens = scanner.scan_tokens()
        self.assertEqual(tokens[0].literal, 5.105)

    def test_identifiers(self):
        # parse user-defined identifier
        scanner = Scanner("my_variable = 10")
        tokens = scanner.scan_tokens()
        expected = [
            (TokenType.IDENTIFIER, "my_variable", None),
            (TokenType.EQUAL, "=", None),
            (TokenType.NUMBER, "10", 10),
        ]
        self.assertTokens(tokens, expected)

        # parse keyword identifier
        scanner = Scanner("if else")
        tokens = scanner.scan_tokens()
        expected = [
            (TokenType.IF, "if", None),
            (TokenType.ELSE, "else", None),
            (TokenType.EOF, "", None),
        ]
        self.assertTokens(tokens, expected)
