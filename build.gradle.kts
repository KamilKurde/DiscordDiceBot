import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    application
}

group = "com.github.KamilKurde"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.jessecorbett:diskord-bot:2.1.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}