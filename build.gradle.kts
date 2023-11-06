import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.20"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	application
}

group = "com.github.KamilKurde"
version = "2.0"

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.jessecorbett:diskord-bot:5.2.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
}

application {
	mainClass.set("BotKt")
}

tasks.withType(ShadowJar::class.java) {
	archiveClassifier.set("")
}