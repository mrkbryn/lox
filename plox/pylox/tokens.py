class TokenType(object):
    LEFT_PAREN = "LEFT_PAREN"           # (
    RIGHT_PAREN = "RIGHT_PAREN"         # )
    LEFT_BRACE = "LEFT_BRACE"           # [
    RIGHT_BRACE = "RIGHT_BRACE"         # ]
    COMMA = "COMMA"                     # ,
    DOT = "DOT"                         # .
    MINUS = "MINUS"                     # -
    PLUS = "PLUS"                       # +
    SEMICOLON = "SEMICOLON"             # ;
    SLASH = "SLASH"                     # /
    STAR = "STAR"                       # *
    BANG = "BANG"                       # !
    BANG_EQUAL = "BANG_EQUAL"           # !=
    EQUAL = "EQUAL"                     # =
    EQUAL_EQUAL = "EQUAL_EQUAL"         # ==
    GREATER = "GREATER"                 # >
    GREATER_EQUAL = "GREATER_EQUAL"     # >=
    LESS = "LESS"                       # <
    LESS_EQUAL = "LESS_EQUAL"           # <=
    IDENTIFIER = "IDENTIFIER"           # ident
    STRING = "STRING"                   # string
    NUMBER = "NUMBER"                   # number
    AND = "AND"                         # and
    CLASS = "CLASS"                     # class
    ELSE = "ELSE"                       # else
    FALSE = "FALSE"                     # false
    FUN = "FUN"                         # fun
    FOR = "FOR"                         # for
    IF = "IF"                           # if
    NIL = "NIL"                         # nil
    OR = "OR"                           # or
    PRINT = "PRINT"                     # print
    RETURN = "RETURN"                   # return
    SUPER = "SUPER"                     # super
    THIS = "THIS"                       # this
    TRUE = "TRUE"                       # true
    VAR = "VAR"                         # var
    WHILE = "WHILE"                     # while
    EOF = "EOF"                         # eof


KEYWORDS = {
    "and": TokenType.AND,
    "class": TokenType.CLASS,
    "else": TokenType.ELSE,
    "false": TokenType.FALSE,
    "for": TokenType.FOR,
    "fun": TokenType.FUN,
    "if": TokenType.IF,
    "nil": TokenType.NIL,
    "or": TokenType.OR,
    "print": TokenType.PRINT,
    "return": TokenType.RETURN,
    "super": TokenType.SUPER,
    "this": TokenType.THIS,
    "true": TokenType.TRUE,
    "var": TokenType.VAR,
    "while": TokenType.WHILE,
}


class Token(object):
    def __init__(self, token_type, lexeme, literal, line):
        self.token_type = token_type
        self.lexeme = lexeme
        self.literal = literal
        self.line = line

    def __repr__(self):
        return "({} {} {})".format(self.token_type, self.lexeme, self.literal)
