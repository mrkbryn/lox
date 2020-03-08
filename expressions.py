
class Expr(object):
    def accept(self, visitor):
        return visitor.visit(self)


class Stmt(object):
    def accept(self, visitor):
        return visitor.visit(self)


class Assign(Expr):
    def __init__(self, name, value):
        self.name = name
        self.value = value

    def __repr__(self):
        return "Assign({}, {})".format(self.name, self.value)


class BinaryExpr(Expr):
    def __init__(self, left, operator, right):
        self.left = left
        self.operator = operator
        self.right = right

    def __repr__(self):
        return "BinaryExpr({}, {}, {})".format(self.left, self.operator, self.right)


class UnaryExpr(Expr):
    def __init__(self, operator, right):
        self.operator = operator
        self.right = right

    def __repr__(self):
        return "UnaryExpr({}, {})".format(self.operator, self.right)


class LiteralExpr(Expr):
    def __init__(self, value):
        self.value = value

    def __repr__(self):
        return "LiteralExpr({})".format(self.value)


class GroupingExpr(Expr):
    def __init__(self, expression):
        self.expression = expression

    def __repr__(self):
        return "GroupingExpr({})".format(self.expression)


class Variable(Expr):
    def __init__(self, name):
        self.name = name

    def __repr__(self):
        return "Variable({})".format(self.name)


class Expression(Stmt):
    def __init__(self, expression):
        self.expression = expression

    def __repr__(self):
        return "Expression({})".format(self.expression)


class Print(Stmt):
    def __init__(self, expression):
        self.expression = expression

    def __repr__(self):
        return "Print({})".format(self.expression)


class Var(Stmt):
    def __init__(self, name, initializer):
        self.name = name
        self.initializer = initializer

    def __repr__(self):
        return "Var({}, {})".format(self.name, self.initializer)
