//
//  object.h
//  src
//
//  Created by Mark Bryan on 4/7/20.
//  Copyright © 2020 Mark Bryan. All rights reserved.
//

#ifndef object_h
#define object_h

#include <stdio.h>

#include "common.h"
#include "chunk.h"
#include "value.h"

#define OBJ_TYPE(value) (AS_OBJ(value)->type)

// macros to test obj types
#define IS_FUNCTION(value) isObjType(value, OBJ_FUNCTION)
#define IS_NATIVE(value)   isObjType(value, OBJ_NATIVE)
#define IS_STRING(value)   isObjType(value, OBJ_STRING)

// macros to cast to types
#define AS_FUNCTION(value) ((ObjFunction*)AS_OBJ(value))
#define AS_NATIVE(value)   (((ObjNative*)AS_OBJ(value))->function)
#define AS_STRING(value)   ((ObjString*)AS_OBJ(value))
#define AS_CSTRING(value)  (((ObjString*)AS_OBJ(value))->chars)

typedef enum {
    OBJ_FUNCTION,
    OBJ_NATIVE,
    OBJ_STRING,
} ObjType;

struct sObj {
    ObjType type;
    struct sObj* next;
};

typedef struct {
    Obj obj;
    int arity; // the number of parameters the function expects
    Chunk chunk;
    ObjString* name;
} ObjFunction;

typedef Value (*NativeFn)(int argCount, Value* args);

typedef struct {
    Obj obj;
    NativeFn function;
} ObjNative;

struct sObjString {
    // C NOTE:
    // Since obj is the first defined field, the struct fields of Obj are
    // expanded in place so that sObjString contains all of the memory/state
    // of an sObj right at the beginning of the object. This allows us to cast
    // an sObjString to an sObj!
    Obj obj;
    int length;
    char* chars;
    uint32_t hash;
};

ObjFunction* newFunction();
ObjNative* newNative(NativeFn function);
ObjString* takeString(char* chars, int length);
ObjString* copyString(const char* chars, int length);
void printObject(Value value);

static inline bool isObjType(Value value, ObjType type) {
    return IS_OBJ(value) && AS_OBJ(value)->type == type;
}

#endif /* object_h */
