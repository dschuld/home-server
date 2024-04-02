import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.franzbecker.gradle-lombok") version "5.0.0"
    id("com.avast.gradle.docker-compose") version "0.16.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

group = "net.davidschuld"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to "net.davidschuld.homeserver.MainKt",
                "Class-Path" to configurations.runtimeClasspath.get().files.joinToString(" ") { it.name }
            )
        )
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("io.ktor:ktor-server-core:1.6.3")
    implementation("io.ktor:ktor-server-netty:1.6.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.ktor:ktor-client-core:1.6.3")
    implementation("io.ktor:ktor-client-cio:1.6.3")
    testImplementation("io.ktor:ktor-server-tests:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("io.ktor:ktor-network-tls-certificates:1.6.3")
    implementation("com.typesafe:config:1.4.1")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
}


