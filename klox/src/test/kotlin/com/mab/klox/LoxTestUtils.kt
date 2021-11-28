package com.mab.klox

import com.mab.klox.cli.Lox
import java.io.ByteArrayOutputStream
import java.io.PrintStream

fun runScript(source: String): String {
    // TODO: determine better pattern to clear runtime state from previous tests.
    Lox.resetRuntime()

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
        Lox.loxRuntime.run(source)
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
