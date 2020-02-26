import sys
from enum import Enum, auto


class TokenType(Enum):
    LEFT_PAREN = auto()
    RIGHT_PARENT = auto()
    LEFT_BRACE = auto()
    RIGHT_BRACE = auto()
    COMMA = auto()
    DOT = auto()
    MINUS = auto()
    PLUS = auto()
    SEMICOLON = auto()
    SLASH = auto()
    STAR = auto()
    BANG = auto()
    BANG_EQUAL = auto()
    EQUAL = auto()
    EQUAL_EQUAL = auto()
    GREATER = auto()
    GREATER_EQUAL = auto()
    LESS = auto()
    LESS_EQUAL = auto()
    IDENTIFIER = auto()
    STRING = auto()
    NUMBER = auto()
    AND = auto()
    CLASS = auto()
    ELSE = auto()
    FALSE = auto()
    FUN = auto()
    FOR = auto()
    IF = auto()
    NIL = auto()
    OR = auto()
    PRINT = auto()
    RETURN = auto()
    SUPER = auto()
    THIS = auto()
    TRUE = auto()
    VAR = auto()
    WHILE = auto()
    EOF = auto()


class Token(object):
    def __init__(self, token_type, lexeme, literal, line):
        self.token_type = token_type
        self.lexeme = lexeme
        self.literal = literal
        self.line = line


class Lox(object):
    def __init__(self):
        self.line = 0
        self.had_error = False

    def run(self, source):
        print("Running source: {}".format(source))

    def error(self, line, msg):
        self.report(line, "", msg)

    def report(self, line, where, msg):
        print("[line {}] Error{}: {}".format(line, where, msg))


def run_file(script):
    print("Running with source: {}".format(script))
    with open(script) as f:
        for line in f:
            f.run(line)


def run_prompt():
    lox = Lox()
    while True:
        line = input("> ")
        lox.run(line)


if __name__ == "__main__":
    if len(sys.argv) > 2:
        print("Usage: pylox [script]")
        exit(1)
    if len(sys.argv) > 1:
        run_file(sys.argv[1])
    else:
        run_prompt()
