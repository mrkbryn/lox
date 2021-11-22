package com.mab.lox.utils

fun isTruthy(obj: Any?): Boolean {
    if (obj == null) return false
    if (obj is Boolean) return obj
    return true
}

fun isEqual(a: Any?, b: Any?): Boolean {
    if (a == null && b == null) return true
    if (a == null) return false
    return a == b
}

fun stringify(obj: Any?): String {
    if (obj == null) return "nil"
    if (obj is Double) {
        var text = obj.toString()
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length - 2)
        }
        return text
    }

    return obj.toString()
}

fun isAlpha(c: Char) = (c in 'a'..'z') || (c in 'A'..'Z') || c == '_'

fun isDigit(c: Char) = c in '0'..'9'

fun isAlphaNumeric(c: Char) = isAlpha(c) || isDigit(c)
