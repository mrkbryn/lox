package com.mab.lox.bytecode

enum class OpCode(val byte: Byte) {
    OP_CONSTANT(0),
    OP_RETURN(1),
}

class Chunk (
    private var count: Int = 0,
    private var capacity: Int = 0,
    private var code: MutableList<Byte> = mutableListOf(),
    private var constants: ValueArray = ValueArray()
) {
    fun write(byte: Byte) {
        // TODO: this doesn't match C impl.
        code.add(byte)
        capacity = code.size
        count = code.size
    }

    fun disassemble(name: String) {
        println("== $name ==")

        var offset = 0
        while (offset < code.size) {
            offset = disassembleInstruction(offset)
        }
    }

    fun disassembleInstruction(offset: Int): Int {
        println("$offset")
        val instruction = code[offset]
        return when (instruction) {
            OpCode.OP_RETURN.byte -> simpleInstruction("OP_RETURN", offset)
            else -> {
                println("Unknown opcode $instruction")
                return offset + 1
            }
        }
    }

    fun simpleInstruction(name: String, offset: Int): Int {
        println(name)
        return offset + 1
    }

    fun addConstant(value: Value): Int {
        constants.write(value)
        // return the index where the constant was appended so we can locate it later.
        return count - 1
    }
}
