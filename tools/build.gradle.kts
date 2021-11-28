import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
}

group = "com.mab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

task("generateAst", JavaExec::class) {
    description = "Runs the GenerateAst script to output the Kotlin and Java AST files for Expr, Stmt."
    main = "com.mab.lox.tools.GenerateAstKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("src/main/kotlin/com/mab/lox")
}
