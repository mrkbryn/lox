//
//  compiler.h
//  clox
//
//  Created by Mark Bryan on 3/31/20.
//  Copyright © 2020 Mark Bryan. All rights reserved.
//

#ifndef compiler_h
#define compiler_h

#include <stdio.h>
#include <stdbool.h>
#include "chunk.h"

bool compile(const char* source, Chunk* chunk);

#endif /* compiler_h */
