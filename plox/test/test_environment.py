import unittest

from pylox.environment import Environment
from pylox.exceptions import RuntimeException
from pylox.tokens import Token, TokenType


class TestEnvironment(unittest.TestCase):
    def setUp(self):
        self.env = Environment()
        self.x = Token(TokenType.VAR, "x", "x", 1)
        self.undefined_var = Token(TokenType.VAR, "undefined_var", "undefined_var", 1)

    def test_define_with_value(self):
        self.env.define(self.x.lexeme, 10)
        self.assertEqual(10, self.env.get(self.x))

    def test_defined_without_value(self):
        self.env.define(self.x.lexeme, None)
        self.assertIsNone(self.env.get(self.x))

    def test_get_undefined_var(self):
        self.assertRaises(RuntimeException, lambda: self.env.get(self.undefined_var))

    def test_assign_undefined_var(self):
        self.assertRaises(RuntimeError, lambda: self.env.assign(self.undefined_var, 10))

    def test_assign(self):
        self.env.define(self.x.lexeme, 10)
        self.assertEqual(10, self.env.get(self.x))
        self.env.assign(self.x, 20)
        self.assertEqual(20, self.env.get(self.x))
