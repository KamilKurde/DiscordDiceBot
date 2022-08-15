import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "com.github.KamilKurde"
version = "1.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.jessecorbett:diskord-bot:2.1.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}

application {
    mainClass.set("BotKt")
}

tasks.register<Jar>("fatJar")
{
    dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
    val sourcesMain = sourceSets.main.get()
    val contents = configurations.runtimeClasspath.get()
        .map { if (it.isDirectory) it else zipTree(it) } +
            sourcesMain.output
    from(contents)
}