from expressions import BinaryExpr, UnaryExpr, LiteralExpr, GroupingExpr
from scanner import TokenType


class Interpreter(object):
    def interpret(self, expr):
        try:
            value = self.evaluate(expr)
            print(value)
        except Exception as e:
            print("Runtime Exception: {}".format(e))

    def evaluate(self, expr):
        return expr.accept(self)

    def visit(self, expr):
        if isinstance(expr, BinaryExpr):
            return self.visit_binary_expr(expr)
        elif isinstance(expr, UnaryExpr):
            return self.visit_unary_expr(expr)
        elif isinstance(expr, LiteralExpr):
            return self.visit_literal_expr(expr)
        elif isinstance(expr, GroupingExpr):
            return self.visit_grouping_expr(expr)

    def visit_literal_expr(self, expr):
        return expr.value

    def visit_grouping_expr(self, expr):
        return self.evaluate(expr.expression)

    def visit_unary_expr(self, expr):
        right = self.evaluate(expr.right)
        if expr.operator.token_type == TokenType.MINUS:
            return -1 * right
        if expr.operator.token_type == TokenType.BANG:
            return not self.is_truthy(right)
        return None

    def visit_binary_expr(self, expr):
        left = self.evaluate(expr.left)
        right = self.evaluate(expr.right)
        operator_type = expr.operator.token_type
        if operator_type == TokenType.GREATER:
            return left > right
        if operator_type == TokenType.GREATER_EQUAL:
            return left >= right
        if operator_type == TokenType.LESS:
            return left < right
        if operator_type == TokenType.LESS_EQUAL:
            return left <= right
        if operator_type == TokenType.MINUS:
            return left - right
        if operator_type == TokenType.SLASH:
            return left / right
        if operator_type == TokenType.STAR:
            return left * right
        if operator_type == TokenType.PLUS:
            return left + right
        if operator_type == TokenType.EQUAL_EQUAL:
            return self.is_equal(left, right)
        if operator_type == TokenType.BANG_EQUAL:
            return not self.is_equal(left, right)
        return None

    def is_truthy(self, obj):
        if obj is None:
            return False
        if isinstance(obj, bool):
            return
        return True

    def is_equal(self, obj1, obj2):
        if obj1 is None and obj2 is None:
            return True
        if obj1 is None:
            return False
        return obj1 == obj2
