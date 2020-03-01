
class Expr(object):
    def accept(self, visitor):
        visitor.visit(self)


class BinaryExpr(Expr):
    def __init__(self, left, operator, right):
        self.left = left
        self.operator = operator
        self.right = right

    def __repr__(self):
        return "BinaryExpr"


class UnaryExpr(Expr):
    def __init__(self, operator, right):
        self.operator = operator
        self.right = right

    def __repr__(self):
        return "UnaryExpr"


class LiteralExpr(Expr):
    def __init__(self, value):
        self.value = value

    def __repr__(self):
        return "LiteralExpr"


class GroupingExpr(Expr):
    def __init__(self, expression):
        self.expression = expression

    def __repr__(self):
        return "GroupingExpr"


