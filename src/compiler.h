//
//  compiler.h
//  src
//
//  Created by Mark Bryan on 3/31/20.
//  Copyright © 2020 Mark Bryan. All rights reserved.
//

#ifndef compiler_h
#define compiler_h

#include <stdio.h>
#include <stdbool.h>

#include "object.h"
#include "chunk.h"

ObjFunction* compile(const char* source);

#endif /* compiler_h */
