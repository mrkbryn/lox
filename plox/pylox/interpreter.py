import logging

from pylox.expressions import BinaryExpr, UnaryExpr, LiteralExpr, GroupingExpr, Print, Expression, Var, Variable, Assign
from pylox.tokens import TokenType
from pylox.exceptions import RuntimeException
from pylox.environment import Environment

logger = logging.getLogger("pylox.interpreter")


class Interpreter(object):
    def __init__(self):
        self.environment = Environment()

    def interpret(self, statements):
        try:
            for stmt in statements:
                self._execute(stmt)
        except Exception as e:
            logger.error("Runtime Exception: {}".format(e))
            print("Runtime Exception: {}".format(e))

    def evaluate(self, expr):
        return expr.accept(self)

    def visit(self, expr):
        if isinstance(expr, BinaryExpr):
            return self._visit_binary_expr(expr)
        elif isinstance(expr, UnaryExpr):
            return self._visit_unary_expr(expr)
        elif isinstance(expr, LiteralExpr):
            return self._visit_literal_expr(expr)
        elif isinstance(expr, GroupingExpr):
            return self._visit_grouping_expr(expr)
        elif isinstance(expr, Print):
            return self._visit_print_stmt(expr)
        elif isinstance(expr, Expression):
            return self._visit_expression_stmt(expr)
        elif isinstance(expr, Var):
            return self._visit_var_stmt(expr)
        elif isinstance(expr, Variable):
            return self._visit_variable_expr(expr)
        elif isinstance(expr, Assign):
            return self._visit_assign_expr(expr)

    def _visit_literal_expr(self, expr):
        return expr.value

    def _visit_grouping_expr(self, expr):
        return self.evaluate(expr.expression)

    def _visit_unary_expr(self, expr):
        right = self.evaluate(expr.right)
        if expr.operator.token_type == TokenType.MINUS:
            self._check_number_operand(expr.operator, right)
            return -1 * right
        if expr.operator.token_type == TokenType.BANG:
            return not self._is_truthy(right)
        return None

    def _visit_binary_expr(self, expr):
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
            return self._is_equal(left, right)
        if operator_type == TokenType.BANG_EQUAL:
            return not self._is_equal(left, right)
        return None

    def _visit_expression_stmt(self, stmt):
        self.evaluate(stmt.expression)
        return None

    def _visit_print_stmt(self, stmt):
        value = self.evaluate(stmt.expression)
        print(value)
        return None

    def _visit_var_stmt(self, stmt):
        if stmt.initializer:
            value = self.evaluate(stmt.initializer)
        else:
            value = None
        self.environment.define(stmt.name.lexeme, value)
        return None

    def _visit_variable_expr(self, expr):
        return self.environment.get(expr.name)

    def _visit_assign_expr(self, expr):
        value = self.evaluate(expr.value)
        self.environment.assign(expr.name, value)
        return value

    def _execute(self, stmt):
        return stmt.accept(self)

    def _is_truthy(self, obj):
        if obj is None:
            return False
        if isinstance(obj, bool):
            return
        return True

    def _is_equal(self, obj1, obj2):
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
