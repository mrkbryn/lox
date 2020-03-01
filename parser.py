from expressions import BinaryExpr, UnaryExpr, LiteralExpr, GroupingExpr
from pylox import TokenType


class Parser(object):
    def __init__(self, tokens):
        self.tokens = tokens
        self.current = 0

    def advance(self):
        if not self.is_at_end():
            self.current += 1
        return self.previous()

    def previous(self):
        return self.tokens[self.current - 1]

    def is_at_end(self):
        return self.peek().token_type == TokenType.EOF

    def peek(self):
        return self.tokens[self.current]

    def check(self, token_type):
        if self.is_at_end():
            return False
        return self.peek().token_type == token_type

    def match(self, token_types):
        for t in token_types:
            if self.check(t):
                self.advance()
                return True
        return False

    def expression(self):
        raise Exception("TODO")

    def equality(self):
        raise Exception("TODO")

    def comparison(self):
        raise Exception("TODO")

    def addition(self):
        raise Exception("TODO")

    def multiplication(self):
        raise Exception("TODO")

    def unary(self):
        raise Exception("TODO")

    def primary(self):
        raise Exception("TODO")


if __name__ == "__main__":
    tokens = []
    parser = Parser(tokens)
