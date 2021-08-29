package com.mab.lox

class RuntimeError(val token: Token, override val message: String) : RuntimeException(message)
