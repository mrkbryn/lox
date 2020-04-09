//
//  object.h
//  clox
//
//  Created by Mark Bryan on 4/7/20.
//  Copyright © 2020 Mark Bryan. All rights reserved.
//

#ifndef object_h
#define object_h

#include <stdio.h>

#include "common.h"
#include "value.h"

#define OBJ_TYPE(value) (AS_OBJ(value)->type)

#define IS_STRING(value) isObjType(value, OBJ_STRING)

#define AS_STRING(value) ((ObjString*)AS_OBJ(value))
#define AS_CSTRING(value) (((ObjString*)AS_OBJ(value))->chars)

typedef enum {
    OBJ_STRING,
} ObjType;

struct sObj {
    ObjType type;
    struct sObj* next;
};

struct sObjString {
    // C NOTE:
    // Since obj is the first defined field, the struct fields of Obj are
    // expanded in place so that sObjString contains all of the memory/state
    // of an sObj right at the beginning of the object. This allows us to cast
    // an sObjString to an sObj!
    Obj obj;
    int length;
    char* chars;
};

ObjString* takeString(char* chars, int length);
ObjString* copyString(const char* chars, int length);
void printObject(Value value);

static inline bool isObjType(Value value, ObjType type) {
    return IS_OBJ(value) && AS_OBJ(value)->type == type;
}

#endif /* object_h */
