def generate_class(name, props):
    print("class {}(Expr):".format(name))
    print("    def __init__(self, {}):".format(', '.join(props)))
    for prop in props:
        print("        self.{} = {}".format(prop, prop))
    print("")
    print("    def __repr__(self):")
    print("        return \"{}\"".format(name))
    print("\n")

expression_template = """
class Expr(object):
    def accept(self, visitor):
        visitor.visit(self)

"""

print(expression_template)
generate_class("BinaryExpr", ["left", "operator", "right"])
generate_class("UnaryExpr", ["operator", "right"])
generate_class("LiteralExpr", ["value"])
generate_class("GroupingExpr", ["expression"])
