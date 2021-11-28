# Lox

Various implementations of Robert Nystrom's Lox language from [Crafting Interpreters](http://www.craftinginterpreters.com/).

## Implementations

In the project subdirectories, there are implementations of the Lox language in a few different languages.

*Tree-Walk Interpreters:*
* Java : jlox
* Python : plox
* Kotlin : klox

*Bytecode Virtual Machines:*
* C : clox

## Running

From the top-level directory, run

```
./gradlew build
```

This will build the jlox and klox projects and run all tests.

## Language Extensions

My implementation of the Lox language adds the following additional features:

1. `+=`, `-=` operators for addition-assignment, subtraction-assignment
2. Additional built-in functions, `exit(exitCode)`, `print(msg)`, `printErr(msg)`
3. Support for integer literals. Number literals without a '.' will be parsed as integer types.
4. Built-in property for an object's class, e.g., `print Foo().klass`

