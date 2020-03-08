import argparse
from scanner import Scanner
from parser import Parser
from interpreter import Interpreter

PYLOX_PROMPT = "> "


class PyLox(object):
    def __init__(self, verbose=False):
        self.verbose = verbose
        self.interpreter = Interpreter(self.verbose)

    def run_prompt(self):
        while True:
            tokens = Scanner(input(PYLOX_PROMPT), verbose=self.verbose).scan_tokens()
            if self.verbose:
                print(tokens)
            parser = Parser(tokens)
            try:
                statements = parser.parse()
            except Exception as e:
                print("Error: {}".format(e))
                continue
            if self.verbose:
                print(" expression -> " + str(statements))
            self.interpreter.interpret(statements)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Pylox interpreter")
    parser.add_argument("--verbose", "-v", dest="verbose", action="store_true")
    args = parser.parse_args()
    pl = PyLox(verbose=args.verbose)
    pl.run_prompt()
