cmake_minimum_required(VERSION 3.17)
project(clox C)

set(CMAKE_C_STANDARD 11)

include_directories(clox)

add_executable(clox
        clox/chunk.c
        clox/chunk.h
        clox/common.h
        clox/compiler.c
        clox/compiler.h
        clox/debug.c
        clox/debug.h
        clox/main.c
        clox/memory.c
        clox/memory.h
        clox/object.c
        clox/object.h
        clox/scanner.c
        clox/scanner.h
        clox/table.c
        clox/table.h
        clox/value.c
        clox/value.h
        clox/vm.c
        clox/vm.h)
