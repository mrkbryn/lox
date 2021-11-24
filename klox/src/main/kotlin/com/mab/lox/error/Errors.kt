package com.mab.lox.error

import com.mab.lox.scanner.Token

/**
 * Errors indicating a Lox runtime check.
 */
class RuntimeError(val token: Token, override val message: String) : RuntimeException(message)

/**
 * Errors indicating failure during the parsing stage.
 */
class ParseError : RuntimeException()
