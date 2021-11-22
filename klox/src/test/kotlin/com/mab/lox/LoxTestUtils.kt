package com.mab.lox

import java.io.ByteArrayOutputStream
import java.io.PrintStream

fun runScript(source: String): String {
    // TODO: redirect System.err?
    // Save old PrintStream
    val printStreamOld = System.out

    // Redirect stdout to a new PrintStream
    val byteArrayOutputStream = ByteArrayOutputStream()
    val printStream = PrintStream(byteArrayOutputStream)
    System.setOut(printStream)
    try {
        // TODO: should we inject some stdout module to capture print statements?
        Lox.run(source)
    } catch (e: Exception) {
        // Ignore.
    } finally {
        // Cleanup and reset System.out
        System.out.flush()
        System.setOut(printStreamOld)
    }

    return byteArrayOutputStream.toString()
}
