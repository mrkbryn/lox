import logging

from pylox.expressions import BinaryExpr, UnaryExpr, LiteralExpr, GroupingExpr, \
    Print, Expression, Var, Variable, Assign
from pylox.tokens import TokenType

logger = logging.getLogger("pylox.parser")


class Parser(object):
    def __init__(self, tokens, verbose=False):
        self.verbose = verbose
        self.tokens = tokens
        self.current = 0

    def _advance(self):
        if not self._is_at_end():
            self.current += 1
        return self._previous()

    def _previous(self):
        return self.tokens[self.current - 1]

    def _is_at_end(self):
        return self._peek().token_type == TokenType.EOF

    def _peek(self):
        return self.tokens[self.current]

    def _check(self, token_type):
        if self._is_at_end():
            return False
        return self._peek().token_type == token_type

    def _match(self, token_types):
        for t in token_types:
            if self._check(t):
                self._advance()
                return True
        return False

    def _consume(self, token_type, message):
        if self._check(token_type):
            return self._advance()
        self._error(self._peek(), message)

    def _error(self, token, message):
        raise Exception(message)

    def parse(self):
        statements = []
        while not self._is_at_end():
            statements.append(self._parse_declaration())
        return statements

    def _parse_statement(self):
        if self._match([TokenType.PRINT]):
            return self._parse_print_statement()
        return self._parse_expression_statement()

    def _parse_declaration(self):
        try:
            if self._match([TokenType.VAR]):
                return self._parse_var_declaration()
            return self._parse_statement()
        except Exception as e:
            self._synchronize()
            return None

    def _parse_var_declaration(self):
        name = self._consume(TokenType.IDENTIFIER, "Expected variable name.")
        if self._match([TokenType.EQUAL]):
            initializer = self._parse_expression()
        self._consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.")
        return Var(name, initializer)

    def _synchronize(self):
        # TODO
        pass

    def _parse_print_statement(self):
        value = self._parse_expression()
        self._consume(TokenType.SEMICOLON, "Expect ';' after value.")
        return Print(value)

    def _parse_expression_statement(self):
        expr = self._parse_expression()
        self._consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return Expression(expr)

    def _parse_expression(self):
        return self._parse_assignment()

    def _parse_assignment(self):
        expr = self._parse_equality()

        if self._match([TokenType.EQUAL]):
            equals = self._previous()
            value = self._parse_assignment()
            if isinstance(expr, Variable):
                name = expr.name
                return Assign(name, value)
            self._error(equals, "Invalid assignment target.")

        return expr

    def _parse_equality(self):
        expr = self._parse_comparison()
        while self._match([TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL]):
            operator = self._previous()
            right = self._parse_comparison()
            expr = BinaryExpr(expr, operator, right)
        return expr

    def _parse_comparison(self):
        expr = self._parse_addition()
        while self._match([TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL]):
            operator = self._previous()
            right = self._parse_addition()
            expr = BinaryExpr(expr, operator, right)
        return expr

    def _parse_addition(self):
        expr = self._parse_multiplication()
        while self._match([TokenType.MINUS, TokenType.PLUS]):
            operator = self._previous()
            right = self._parse_multiplication()
            expr = BinaryExpr(expr, operator, right)
        return expr

    def _parse_multiplication(self):
        expr = self._parse_unary()
        while self._match([TokenType.SLASH, TokenType.STAR]):
            operator = self._previous()
            right = self._parse_unary()
            expr = BinaryExpr(expr, operator, right)
        return expr

    def _parse_unary(self):
        if self._match([TokenType.BANG, TokenType.MINUS]):
            operator = self._previous()
            right = self._parse_unary()
            return UnaryExpr(operator, right)
        return self._parse_primary()

    def _parse_primary(self):
        if self._match([TokenType.FALSE]):
            return LiteralExpr(False)
        if self._match([TokenType.TRUE]):
            return LiteralExpr(True)
        if self._match([TokenType.NIL]):
            return LiteralExpr(None)
        if self._match([TokenType.NUMBER, TokenType.STRING]):
            return LiteralExpr(self._previous().literal)
        if self._match([TokenType.LEFT_PAREN]):
            expr = self._parse_expression()
            self._consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return GroupingExpr(expr)
        if self._match([TokenType.IDENTIFIER]):
            return Variable(self._previous())
