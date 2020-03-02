from expressions import BinaryExpr, UnaryExpr, LiteralExpr, GroupingExpr, Print, Expression
from scanner import TokenType
from exceptions import RuntimeException


class Interpreter(object):
    def interpret(self, statements):
        try:
            for stmt in statements:
                self.execute(stmt)
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
        elif isinstance(expr, Print):
            return self.visit_print_stmt(expr)
        elif isinstance(expr, Expression):
            return self.visit_expression_stmt(expr)

    def visit_literal_expr(self, expr):
        return expr.value

    def visit_grouping_expr(self, expr):
        return self.evaluate(expr.expression)

    def visit_unary_expr(self, expr):
        right = self.evaluate(expr.right)
        if expr.operator.token_type == TokenType.MINUS:
            self._check_number_operand(expr.operator, right)
            return -1 * right
        if expr.operator.token_type == TokenType.BANG:
            return not self.is_truthy(right)
        return None

    def visit_binary_expr(self, expr):
        left = self.evaluate(expr.left)
        right = self.evaluate(expr.right)
        operator_type = expr.operator.token_type
        if operator_type == TokenType.GREATER:
            self._check_number_operands(expr.operator, left, right)
            return left > right
        if operator_type == TokenType.GREATER_EQUAL:
            self._check_number_operands(expr.operator, left, right)
            return left >= right
        if operator_type == TokenType.LESS:
            self._check_number_operands(expr.operator, left, right)
            return left < right
        if operator_type == TokenType.LESS_EQUAL:
            self._check_number_operands(expr.operator, left, right)
            return left <= right
        if operator_type == TokenType.MINUS:
            self._check_number_operands(expr.operator, left, right)
            return left - right
        if operator_type == TokenType.SLASH:
            self._check_number_operands(expr.operator, left, right)
            return left / right
        if operator_type == TokenType.STAR:
            self._check_number_operands(expr.operator, left, right)
            return left * right
        if operator_type == TokenType.PLUS:
            self._check_number_operands(expr.operator, left, right)
            return left + right
        if operator_type == TokenType.EQUAL_EQUAL:
            return self.is_equal(left, right)
        if operator_type == TokenType.BANG_EQUAL:
            return not self.is_equal(left, right)
        return None

    def visit_expression_stmt(self, stmt):
        self.evaluate(stmt.expression)
        return None

    def visit_print_stmt(self, stmt):
        value = self.evaluate(stmt.expression)
        print(value)
        return None

    def execute(self, stmt):
        stmt.accept(self)

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

    def _check_number_operand(self, operator, operand):
        if type(operand) == float:
            return
        raise RuntimeException(operator, "Operand must be a number.")

    def _check_number_operands(self, operator, left, right):
        if type(left) == float and type(right) == float:
            return
        raise RuntimeException(operator, "Operands must be numbers.")
