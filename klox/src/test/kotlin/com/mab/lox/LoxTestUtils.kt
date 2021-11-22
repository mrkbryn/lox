package com.mab.lox

import java.io.ByteArrayOutputStream
import java.io.PrintStream

fun runScript(source: String): String {
    // Save old PrintStream
    val printStreamSysOut = System.out
    val printStreamSysErr = System.err

    // Redirect stdout to a new PrintStream
    val byteArrayOutputStream = ByteArrayOutputStream()
    val printStream = PrintStream(byteArrayOutputStream)
    System.setOut(printStream)
    System.setErr(printStream)
    try {
        // TODO: should we inject some stdout module to capture print statements?
        Lox.run(source)
    } catch (e: Exception) {
        // Ignore.
    } finally {
        // Cleanup and reset System.out
        System.out.flush()
        System.err.flush()
        System.setOut(printStreamSysOut)
        System.setErr(printStreamSysErr)
    }

    return byteArrayOutputStream.toString()
}