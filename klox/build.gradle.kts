import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}

group = "com.mab"
version = "1.0-SNAPSHOT"

val kotestVersion = "4.6.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation(kotlin("test"))

    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
}

tasks.withType<Test> {
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
