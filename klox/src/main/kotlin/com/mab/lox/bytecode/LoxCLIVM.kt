package com.mab.lox.bytecode

fun main() {
    val chunk = Chunk()

    val constant = chunk.addConstant(1.2)
    chunk.write(OpCode.OP_CONSTANT.byte)
    chunk.write(constant.toByte())

    chunk.write(OpCode.OP_RETURN.byte)
    chunk.disassemble("test chunk")
}
