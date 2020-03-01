from expressions import BinaryExpr, UnaryExpr, LiteralExpr, GroupingExpr
from scanner import Token, TokenType, Scanner


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

    def consume(self, token_type, message):
        if self.check(token_type):
            return self.advance()
        self.error(self.peek(), message)

    def error(self, token, message):
        print("ERROR")
        raise Exception(message)

    def expression(self):
        return self.equality()

    def equality(self):
        expr = self.comparison()
        while self.match([TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL]):
            operator = self.previous()
            right = self.comparison()
            expr = BinaryExpr(expr, operator, right)
        return expr

    def comparison(self):
        expr = self.addition()
        while self.match([TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL]):
            operator = self.previous()
            right = self.addition()
            expr = BinaryExpr(expr, operator, right)
        return expr

    def addition(self):
        expr = self.multiplication()
        while self.match([TokenType.MINUS, TokenType.PLUS]):
            operator = self.previous()
            right = self.multiplication()
            expr = BinaryExpr(expr, operator, right)
        return expr

    def multiplication(self):
        expr = self.unary()
        while self.match([TokenType.SLASH, TokenType.STAR]):
            operator = self.previous()
            right = self.unary()
            expr = BinaryExpr(expr, operator, right)
        return expr

    def unary(self):
        if self.match([TokenType.BANG, TokenType.MINUS]):
            operator = self.previous()
            right = self.unary()
            return UnaryExpr(operator, right)
        return self.primary()

    def primary(self):
        if self.match([TokenType.FALSE]):
            return LiteralExpr(False)
        if self.match([TokenType.TRUE]):
            return LiteralExpr(True)
        if self.match([TokenType.NIL]):
            return LiteralExpr(None)
        if self.match([TokenType.NUMBER, TokenType.STRING]):
            return LiteralExpr(self.previous().literal)
        if self.match([TokenType.LEFT_PAREN]):
            expr = self.expression()
            self.consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return GroupingExpr(expr)


if __name__ == "__main__":
    scanner = Scanner("1 + 2.0")
    tokens = scanner.scan_tokens()
    print(tokens)
    parser = Parser(tokens)
    expression = parser.expression()
    print(expression)
