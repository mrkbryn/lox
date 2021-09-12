import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}

group = "com.mab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.mab.lox.MainKt")
}

task("generateAst", JavaExec::class) {
    description = "Runs the GenerateAst script to output the Kotlin AST files for Expr, Stmt."
    main = "com.mab.lox.Generate_astKt"
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("src/main/kotlin")
}
