from exceptions import RuntimeException


class Environment(object):
    def __init__(self, verbose=False):
        self.verbose = verbose
        self.values = {}

    def define(self, name, value):
        if self.verbose:
            print("ENV: defining var '{}' to {}".format(name, value))
        self.values[name] = value

    def get(self, name):
        if name.lexeme in self.values:
            return self.values[name.lexeme]
        raise RuntimeException(name.lexeme, "Undefined variable '{}'.".format(name.lexeme))

    def assign(self, name, value):
        if name.lexeme in self.values:
            self.values[name.lexeme] = value
            return
        raise RuntimeError("Undefined variable '{}'".format(name.lexeme))

    def __repr__(self):
        return "Environment({})".format(self.values)


if __name__ == "__main__":
    env = Environment()
    env.define("x", 10)
    print(env.get("x"))
