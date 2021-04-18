import logging

from pylox.expressions import BinaryExpr, UnaryExpr, LiteralExpr, GroupingExpr, Print, Expression, Var, Variable, Assign
from pylox.tokens import TokenType

logger = logging.getLogger("pylox.parser")


class Parser(object):
    def __init__(self, tokens, verbose=False):
        self.verbose = verbose
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
        raise Exception(message)

    def parse(self):
        statements = []
        while not self.is_at_end():
            statements.append(self.declaration())
        return statements

    def statement(self):
        if self.match([TokenType.PRINT]):
            return self.print_statement()
        return self.expression_statement()

    def declaration(self):
        try:
            if self.match([TokenType.VAR]):
                return self.var_declaration()
            return self.statement()
        except Exception as e:
            self.synchronize()
            return None

    def var_declaration(self):
        name = self.consume(TokenType.IDENTIFIER, "Expected variable name.")
        if self.match([TokenType.EQUAL]):
            initializer = self.expression()
        self.consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.")
        return Var(name, initializer)

    def synchronize(self):
        pass

    def print_statement(self):
        value = self.expression()
        self.consume(TokenType.SEMICOLON, "Expect ';' after value.")
        return Print(value)

    def expression_statement(self):
        expr = self.expression()
        self.consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return Expression(expr)

    def expression(self):
        return self.assignment()

    def assignment(self):
        expr = self.equality()

        if self.match([TokenType.EQUAL]):
            equals = self.previous()
            value = self.assignment()
            if isinstance(expr, Variable):
                name = expr.name
                return Assign(name, value)
            self.error(equals, "Invalid assignment target.")

        return expr

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
        if self.match([TokenType.IDENTIFIER]):
            return Variable(self.previous())
