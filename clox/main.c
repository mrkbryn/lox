//
//  main.c
//  clox
//
//  Created by Mark Bryan on 3/29/20.
//  Copyright © 2020 Mark Bryan. All rights reserved.
//

#include <stdio.h>
#include "common.h"
#include "chunk.h"
#include "debug.h"
#include "vm.h"

int main(int argc, const char * argv[]) {
    initVM();
    
    Chunk chunk;
    initChunk(&chunk);
    
    // "compile" into the VM: -((1.2 + 3.4) / 5.6)
    
    // 1.2
    int constant = addConstant(&chunk, 1.2);
    writeChunk(&chunk, OP_CONSTANT, 10);
    writeChunk(&chunk, constant, 10);
    
    // 3.4
    constant = addConstant(&chunk, 3.4);
    writeChunk(&chunk, OP_CONSTANT, 10);
    writeChunk(&chunk, constant, 10);
    
    // 1.2 + 3.4
    writeChunk(&chunk, OP_ADD, 10);
    
    // 5.6
    constant = addConstant(&chunk, 5.6);
    writeChunk(&chunk, OP_CONSTANT, 10);
    writeChunk(&chunk, constant, 10);
    
    // divide the result
    writeChunk(&chunk, OP_DIVIDE, 10);
    
    // negate the result
    writeChunk(&chunk, OP_NEGATE, 10);
    
    // finally, return the result
    writeChunk(&chunk, OP_RETURN, 10);
    
    interpret(&chunk);
    
    freeVM();
    freeChunk(&chunk);
    
    return 0;
}
