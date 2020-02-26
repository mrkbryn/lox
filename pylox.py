import sys
from enum import Enum, auto


class TokenType(Enum):
    LEFT_PAREN = auto()
    RIGHT_PAREN = auto()
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

    def __repr__(self):
        return "({} {} {})".format(self.token_type, self.lexeme, self.literal)


class Lox(object):
    def __init__(self):
        self.had_error = False

    def run(self, source):
        print("Running source: {}".format(source))

    def error(self, line, msg):
        self.report(line, "", msg)

    def report(self, line, where, msg):
        print("[line {}] Error{}: {}".format(line, where, msg))


class Scanner(object):
    def __init__(self, source):
        self.source = source
        self.start = 0
        self.current = 0
        self.line = 1
        self.tokens = []

    def is_at_end(self):
        return self.current >= len(self.source)

    def scan_tokens(self):
        while not self.is_at_end():
            self.start = self.current
            self.scan_token()
        self.tokens.append(Token(TokenType.EOF, "", None, self.line))
        return self.tokens

    def add_token(self, token_type, literal=None):
        text = self.source[self.start:self.current]
        self.tokens.append(Token(token_type, text, literal, self.line))

    def advance(self):
        self.current += 1
        return self.source[self.current - 1]

    def match(self, expected):
        if self.is_at_end() or self.source[self.current] != expected:
            return False
        self.current += 1
        return True

    def peek(self):
        return '\0' if self.is_at_end() else self.source[self.current]

    def scan_token(self):
        c = self.advance()
        if c == '(':
            self.add_token(TokenType.LEFT_PAREN)
        elif c == ')':
            self.add_token(TokenType.RIGHT_PAREN)
        elif c == '{':
            self.add_token(TokenType.LEFT_BRACE)
        elif c == '}':
            self.add_token(TokenType.RIGHT_BRACE)
        elif c == ',':
            self.add_token(TokenType.COMMA)
        elif c == '.':
            self.add_token(TokenType.DOT)
        elif c == '-':
            self.add_token(TokenType.MINUS)
        elif c == '+':
            self.add_token(TokenType.PLUS)
        elif c == ';':
            self.add_token(TokenType.SEMICOLON)
        elif c == '*':
            self.add_token(TokenType.STAR)
        elif c == '!':
            self.add_token(TokenType.BANG_EQUAL if self.match("=") else TokenType.BANG)
        elif c == '=':
            self.add_token(TokenType.EQUAL_EQUAL if self.match("=") else TokenType.EQUAL)
        elif c == '<':
            self.add_token(TokenType.LESS_EQUAL if self.match("=") else TokenType.LESS)
        elif c == '>':
            self.add_token(TokenType.GREATER_EQUAL if self.match("=") else TokenType.GREATER)
        elif c == '/':
            if self.match('/'):
                # comments go the end of the line
                while self.peek() != '\n' and not self.is_at_end():
                    self.advance()
            else:
                self.add_token(TokenType.SLASH)
        elif c in ' \r\t':
            # ignore whitespace
            pass
        elif c == '\n':
            self.line += 1
        elif c == '"':
            self.string()
        else:
            print("ERROR!! Unexpected character: '{}'".format(c))

    def string(self):
        while self.peek() != '"' and not self.is_at_end():
            if self.peek() == '\n':
                # multiline string!
                self.line += 1
            self.advance()

        if self.is_at_end():
            # unterminated string!
            print("ERROR! unterminated string!")
            return

        # move past close of string
        self.advance()
        text = self.source[self.start + 1:self.current - 1]
        self.add_token(TokenType.STRING, text)

def run_file(script):
    print("Running with source: {}".format(script))
    with open(script) as f:
        for line in f:
            f.run(line)


def run_prompt():
    while True:
        line = input("> ")
        scanner = Scanner(line)
        tokens = scanner.scan_tokens()
        print(tokens)


if __name__ == "__main__":
    if len(sys.argv) > 2:
        print("Usage: pylox [script]")
        exit(1)
    if len(sys.argv) > 1:
        run_file(sys.argv[1])
    else:
        run_prompt()
