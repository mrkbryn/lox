//
//  debug.h
//  src
//
//  Created by Mark Bryan on 3/29/20.
//  Copyright © 2020 Mark Bryan. All rights reserved.
//

#ifndef debug_h
#define debug_h

#include <stdio.h>
#include "chunk.h"

void disassembleChunk(Chunk* chunk, const char* name);
int disassembleInstruction(Chunk* chunk, int offset);

#endif /* debug_h */
