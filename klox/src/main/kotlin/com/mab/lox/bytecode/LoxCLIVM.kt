package com.mab.lox.bytecode

fun main(args: Array<String>) {
    val chunk = Chunk()
    chunk.write(OpCode.OP_RETURN.byte)
    chunk.disassemble("test chunk")
}
