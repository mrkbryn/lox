import sys
from scanner import Scanner
from parser import Parser
from interpreter import Interpreter


class PyLox(object):
    def __init__(self):
        self.interpreter = Interpreter()

    def run_prompt(self):
        while True:
            line = input("> ")
            scanner = Scanner(line)
            tokens = scanner.scan_tokens()
            print(" tokens     -> " + str(tokens))
            parser = Parser(tokens)
            expression = parser.expression()
            print(" expression -> " + str(expression))
            self.interpreter.interpret(expression)


if __name__ == "__main__":
    if len(sys.argv) > 1:
        print("Usage: pylox")
        exit(1)
    else:
        pl = PyLox()
        pl.run_prompt()
