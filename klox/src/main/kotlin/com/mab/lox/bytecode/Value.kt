package com.mab.lox.bytecode

typealias Value = Double

class ValueArray(
    var values: MutableList<Value> = mutableListOf()
) {
    fun write(value: Value) {
        values.add(value)
    }
}

fun Value.formatForPrint(): String {
    return this.toString()
}
