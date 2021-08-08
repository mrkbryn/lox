import unittest

from pylox.scanner import Scanner
from pylox.parser import Parser
from pylox.interpreter import Interpreter

class TestInterpreter(unittest.TestCase):
    def test_pass(self):
        scanner = Scanner("5 + 5;")
        parser = Parser(scanner.scan_tokens())
        interpreter = Interpreter()
        interpreter.interpret(parser.parse())
