//
//  vm.h
//  clox
//
//  Created by Mark Bryan on 3/30/20.
//  Copyright © 2020 Mark Bryan. All rights reserved.
//

#ifndef vm_h
#define vm_h

#include <stdio.h>
#include "chunk.h"

typedef struct {
    Chunk* chunk;
    uint8_t* ip;
} VM;

typedef enum {
    INTERPRET_OK,
    INTERPRET_COMPILE_ERROR,
    INTERPRET_RUNTIME_ERROR
} InterpretResult;

void initVM();
void freeVM();
InterpretResult interpret(Chunk* chunk);

#endif /* vm_h */
