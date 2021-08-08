import logging

from pylox.exceptions import RuntimeException


logger = logging.getLogger("pylox.environment")


class Environment(object):
    """Runtime environment which stores/updates named variables throughout the lifetime of a lox execution."""
    def __init__(self):
        self.values = {}

    def define(self, name, value):
        logger.debug("Defining var '{}' := {}".format(name, value))
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
