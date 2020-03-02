import sys
from scanner import Scanner
from parser import Parser
from interpreter import Interpreter


class PyLox(object):
    def __init__(self):
        self.interpreter = Interpreter()

    def run_prompt(self):
        while True:
            tokens = Scanner(input("> ")).scan_tokens()
            # print(tokens)
            parser = Parser(tokens)
            try:
                statements = parser.parse()
            except Exception as e:
                print("Error: {}".format(e))
                continue
            # print(" expression -> " + str(statements))
            self.interpreter.interpret(statements)


if __name__ == "__main__":
    if len(sys.argv) > 1:
        print("Usage: pylox")
        exit(1)
    else:
        pl = PyLox()
        pl.run_prompt()
