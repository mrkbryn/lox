import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}

group = "me.mabryan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "com.mab.lox.MainKt"
}

task("generateAst", JavaExec::class) {
    description = "Runs the GenerateAst script to output the Kotlin AST files for Expr, Stmt."
    main = "com.mab.lox.Generate_astKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("src/main/kotlin")
}
