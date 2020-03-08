from exceptions import RuntimeException


class Environment(object):
    def __init__(self):
        self.values = {}

    def define(self, name, value):
        self.values[name] = value

    def get(self, name):
        if name in self.values:
            return self.values[name]
        raise RuntimeException(name, "Undefined variable '{}'.".format(name.lexeme))

    def __repr__(self):
        return "Environment({})".format(self.values)
