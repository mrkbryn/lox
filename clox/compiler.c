//
//  compiler.c
//  clox
//
//  Created by Mark Bryan on 3/31/20.
//  Copyright © 2020 Mark Bryan. All rights reserved.
//

#include "common.h"
#include "compiler.h"
#include "scanner.h"

void compile(const char* source) {
    initScanner(source);
    int line = -1;
    for (;;) {
        Token token = scanToken();
        if (token.line != line) {
            // new line in source.. print the line number
            printf("%4d ", token.line);
            line = token.line;
        } else {
            // indicate the previous source line is continued
            printf("   | ");
        }
        
        printf("%s '%.*s'\n", typeName(token.type), token.length, token.start);
        
        if (token.type == TOKEN_EOF) break;
    }
}
