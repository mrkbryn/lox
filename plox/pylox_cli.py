import argparse
from pylox.scanner import Scanner
from pylox.parser import Parser
from pylox.interpreter import Interpreter


PYLOX_PROMPT = "> "


class PyLox(object):
    """
    Command-line runner to execute Lox source code. Provides an interface
    to run as REPL prompt or on a pylox source file.
    """

    def __init__(self, verbose=False):
        self.verbose = verbose
        self.interpreter = Interpreter()

    def run_prompt(self):
        while True:
            source = input(PYLOX_PROMPT)
            self.try_read_and_evaluate(source)

    def run_file(self, path):
        with open(path) as f:
            source = f.read()
            self.try_read_and_evaluate(source)

    def try_read_and_evaluate(self, source):
        tokens = Scanner(source).scan_tokens()
        if self.verbose:
            print(" tokens -> {}".format(tokens))
        parser = Parser(tokens)
        try:
            statements = parser.parse()
        except Exception as e:
            print("Error parsing statements: {}".format(e))
            return

        if self.verbose:
            print(" statements -> {}".format(statements))
        self.interpreter.interpret(statements)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Pylox interpreter")
    parser.add_argument("--verbose", "-v", dest="verbose", action="store_true")
    parser.add_argument("-i", dest="input", required=False)
    args = parser.parse_args()

    pl = PyLox(verbose=args.verbose)
    if args.input:
        pl.run_file(args.input)
    else:
        pl.run_prompt()
