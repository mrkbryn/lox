import sys
from scanner import Scanner
from parser import Parser


def run_prompt():
    while True:
        line = input("> ")
        scanner = Scanner(line)
        tokens = scanner.scan_tokens()
        print(" tokens     -> " + str(tokens))
        parser = Parser(tokens)
        expression = parser.expression()
        print(" expression -> " + str(expression))


if __name__ == "__main__":
    if len(sys.argv) > 1:
        print("Usage: pylox")
        exit(1)
    else:
        run_prompt()
