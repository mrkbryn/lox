def generate_class(name, super_class, props):
    print("class {}({}):".format(name, super_class))
    print("    def __init__(self, {}):".format(', '.join(props)))
    for prop in props:
        print("        self.{} = {}".format(prop, prop))
    print("")
    print("    def __repr__(self):")
    prop_list = ", ".join(["self.{}".format(i) for i in props])
    formatter = name + "(" + "{}"
    for i in range(1, len(props)):
        formatter += ", {}"
    formatter += ")"
    print("        return \"{}\".format({})".format(formatter, prop_list))
    print("\n")

expression_template = """
class Expr(object):
    def accept(self, visitor):
        visitor.visit(self)

"""

print(expression_template)
generate_class("BinaryExpr", "Expr", ["left", "operator", "right"])
generate_class("UnaryExpr", "Expr", ["operator", "right"])
generate_class("LiteralExpr", "Expr", ["value"])
generate_class("GroupingExpr", "Expr", ["expression"])
