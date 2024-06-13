import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	kotlin("jvm") version "2.0.0"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	kotlin("plugin.serialization") version "2.0.0"
	application
}

group = "com.github.KamilKurde"
version = "2.2.0"

repositories {
	mavenCentral()
}

dependencies {
	implementation("dev.kord:kord-core:0.14.0")
}

application {
	mainClass.set("BotKt")
}

tasks.withType(ShadowJar::class.java) {
	archiveClassifier.set("")
}