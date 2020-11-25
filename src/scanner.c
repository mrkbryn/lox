//
//  scanner.c
//  src
//
//  Created by Mark Bryan on 3/31/20.
//  Copyright © 2020 Mark Bryan. All rights reserved.
//

#include <string.h>
#include <stdio.h>
#include <stdbool.h>

#include "scanner.h"

typedef struct {
    const char* start;
    const char* current;
    int line;
} Scanner;

Scanner scanner;

void initScanner(const char* source) {
    // position the scanner at the start of the source on line 1
    scanner.start = source;
    scanner.current = source;
    scanner.line = 1;
}

static bool isAtEnd() {
    return *scanner.current == '\0';
}

static Token makeToken(TokenType type) {
    Token token;
    token.type = type;
    token.start = scanner.start;
    token.length = (int)(scanner.current - scanner.start);
    token.line = scanner.line;
    return token;
}

static Token errorToken(const char* message) {
    Token token;
    token.type = TOKEN_ERROR;
    token.start = message;
    token.length = (int)strlen(message);
    token.line = scanner.line;
    return token;
}

static char advance() {
    scanner.current++;
    return scanner.current[-1];
}

static char peek() {
    return *scanner.current;
}

static char peekNext() {
    if (isAtEnd()) return '\0';
    return scanner.current[1];
}

static bool match(char expected) {
    if (isAtEnd() || *scanner.current != expected) return false;
    
    // consume the match
    scanner.current++;
    return true;
}

static void skipWhitespace() {
    for (;;) {
        char c = peek();
        switch (c) {
            case ' ':
            case '\r':
            case '\t':
                advance();
                break;
            case '\n':
                scanner.line++;
                advance();
                break;
            case '/':
                if (peekNext() == '/') {
                    // consume comment until end of line
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    return;
                }
                break;
                
            default:
                return;
        }
    }
}

static Token string() {
    while (peek() != '"' && !isAtEnd()) {
        if (peek() == '\n') scanner.line++;
        advance();
    }
    
    if (isAtEnd()) return errorToken("Unterminated string.");
    
    // consume closing quote
    advance();
    return makeToken(TOKEN_STRING);
}

static bool isDigit(char c) {
    return c >= '0' && c <= '9';
}

static bool isAlpha(char c) {
    return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_';
}

static Token number() {
    while (isDigit(peek())) advance();
    
    if (peek() == '.' && isDigit(peekNext())) {
        // consume the "."
        advance();
        while (isDigit(peek())) advance();
    }
    
    return makeToken(TOKEN_NUMBER);
}

static TokenType checkKeyword(int start, int length, const char* rest, TokenType type) {
    if (scanner.current - scanner.start == start + length && memcmp(scanner.start + start, rest, length) == 0) {
        return type;
    }
    return TOKEN_IDENTIFIER;
}

static TokenType identifierType() {
    // we could look up the keyword via hashing, but we opt
    // for a pseudo-trie check to determine if we've "identified"
    // a lox keyword
    switch (scanner.start[0]) {
        case 'a': return checkKeyword(1, 2, "nd", TOKEN_AND);
        case 'c': return checkKeyword(1, 4, "lass", TOKEN_CLASS);
        case 'e': return checkKeyword(1, 3, "lse", TOKEN_ELSE);
        case 'f':
            if (scanner.current - scanner.start > 1) {
                // false, for, fun
                switch (scanner.start[1]) {
                    case 'a': return checkKeyword(2, 3, "lse", TOKEN_FALSE);
                    case 'o': return checkKeyword(2, 1, "r", TOKEN_FOR);
                    case 'u': return checkKeyword(2, 1, "n", TOKEN_FUN);
                }
            }
            break;
        case 'i': return checkKeyword(1, 1, "f", TOKEN_IF);
        case 'n': return checkKeyword(1, 2, "il", TOKEN_NIL);
        case 'o': return checkKeyword(1, 1, "r", TOKEN_OR);
        case 'p': return checkKeyword(1, 4, "rint", TOKEN_PRINT);
        case 'r': return checkKeyword(1, 5, "eturn", TOKEN_RETURN);
        case 's': return checkKeyword(1, 4, "uper", TOKEN_SUPER);
        case 't':
            if (scanner.current - scanner.start > 1) {
                // this, true
                switch (scanner.start[1]) {
                    case 'h': return checkKeyword(2, 2, "is", TOKEN_THIS);
                    case 'r': return checkKeyword(2, 2, "ue", TOKEN_TRUE);
                }
            }
            break;
        case 'v': return checkKeyword(1, 2, "ar", TOKEN_VAR);
        case 'w': return checkKeyword(1, 4, "hile", TOKEN_WHILE);
    }
    
    return TOKEN_IDENTIFIER;
}

static Token identifier() {
    while (isAlpha(peek()) || isDigit(peek())) advance();
    
    return makeToken(identifierType());
}

Token scanToken() {
    skipWhitespace();
    
    scanner.start = scanner.current;
    
    if (isAtEnd()) return makeToken(TOKEN_EOF);
    
    char c = advance();
    if (isAlpha(c)) return identifier();
    if (isDigit(c)) return number();
    switch (c) {
        case '(': return makeToken(TOKEN_LEFT_PAREN);
        case ')': return makeToken(TOKEN_RIGHT_PAREN);
        case '{': return makeToken(TOKEN_LEFT_BRACE);
        case '}': return makeToken(TOKEN_RIGHT_BRACE);
        case ';': return makeToken(TOKEN_SEMICOLON);
        case ',': return makeToken(TOKEN_COMMA);
        case '.': return makeToken(TOKEN_DOT);
        case '-': return makeToken(TOKEN_MINUS);
        case '+': return makeToken(TOKEN_PLUS);
        case '/': return makeToken(TOKEN_SLASH);
        case '*': return makeToken(TOKEN_STAR);
        case '!':
            return makeToken(match('=') ? TOKEN_BANG_EQUAL : TOKEN_BANG);
        case '=':
            return makeToken(match('=') ? TOKEN_EQUAL_EQUAL : TOKEN_EQUAL);
        case '<':
            return makeToken(match('=') ? TOKEN_LESS_EQUAL : TOKEN_LESS);
        case '>':
            return makeToken(match('=') ? TOKEN_GREATER_EQUAL : TOKEN_GREATER);
        case '"': return string();
    }
    
    return errorToken("Unexpected character.");
}

const char* typeName(TokenType type) {
    // return a human-readable type
    switch (type) {
        case TOKEN_LEFT_PAREN: return "LEFT_PAREN";
        case TOKEN_RIGHT_PAREN: return "RIGHT_PAREN";
        case TOKEN_LEFT_BRACE: return "LEFT_BRACE";
        case TOKEN_RIGHT_BRACE: return "RIGHT_BRACE";
        case TOKEN_COMMA: return "COMMA";
        case TOKEN_DOT: return "DOT";
        case TOKEN_MINUS: return "MINUS";
        case TOKEN_PLUS: return "PLUS";
        case TOKEN_SEMICOLON: return "SEMICOLON";
        case TOKEN_SLASH: return "SLASH";
        case TOKEN_STAR: return "STAR";
        case TOKEN_BANG: return "BANG";
        case TOKEN_BANG_EQUAL: return "BANG_EQUAL";
        case TOKEN_EQUAL: return "EQUAL";
        case TOKEN_EQUAL_EQUAL: return "EQUAL_EQUAL";
        case TOKEN_GREATER: return "GREATER";
        case TOKEN_GREATER_EQUAL: return "GREATER_EQUAL";
        case TOKEN_LESS: return "LESS";
        case TOKEN_LESS_EQUAL: return "LESS_EQUAL";
        case TOKEN_IDENTIFIER: return "IDENTIFIER";
        case TOKEN_STRING: return "STRING";
        case TOKEN_NUMBER: return "NUMBER";
        case TOKEN_AND: return "AND";
        case TOKEN_CLASS: return "CLASS";
        case TOKEN_ELSE: return "ELSE";
        case TOKEN_FALSE: return "FALSE";
        case TOKEN_FOR: return "FOR";
        case TOKEN_FUN: return "FUN";
        case TOKEN_IF: return "IF";
        case TOKEN_NIL: return "NIL";
        case TOKEN_OR: return "OR";
        case TOKEN_PRINT: return "PRINT";
        case TOKEN_RETURN: return "RETURN";
        case TOKEN_SUPER: return "SUPER";
        case TOKEN_THIS: return "THIS";
        case TOKEN_TRUE: return "TRUE";
        case TOKEN_VAR: return "VAR";
        case TOKEN_WHILE: return "WHILE";
        case TOKEN_ERROR: return "ERROR";
        case TOKEN_EOF: return "EOF";
        default: return "UNKNOWN";
    }
}
