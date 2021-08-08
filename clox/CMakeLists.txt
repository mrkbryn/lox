cmake_minimum_required(VERSION 3.17)
project(clox C)

set(CMAKE_C_STANDARD 11)

include_directories(src)

add_library(clox_lib
        src/chunk.c
        src/chunk.h
        src/common.h
        src/compiler.c
        src/compiler.h
        src/debug.c
        src/debug.h
        src/memory.c
        src/memory.h
        src/object.c
        src/object.h
        src/scanner.c
        src/scanner.h
        src/table.c
        src/table.h
        src/value.c
        src/value.h
        src/vm.c
        src/vm.h)

add_executable(clox
        src/main.c)

target_link_libraries(clox
        clox_lib)
