import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
}

group = "fairu"
version = "1.0"

application {
    mainClass.set("fairu.LauncherKt")
}

repositories {
    mavenCentral()
    maven("https://maven.dimensional.fun/releases")
    maven("https://maven.noelware.org")
    maven("https://maven.floofy.dev/repo/releases")
}

dependencies {
    /* kotlin */
    implementation(kotlin("stdlib"))

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // logging
    implementation("io.github.microutils:kotlin-logging:3.0.2")

    /* koin - dependency injection */
    implementation("io.insert-koin:koin-core:3.2.2")

    /* toml - configuration format */
    implementation("com.akuleshov7:ktoml-core:0.2.13")
    implementation("com.akuleshov7:ktoml-file:0.2.13")

    /* naibu - utilities */
    implementation("naibu.stdlib:naibu-core:1.0-RC.5")

    // extensions
    implementation("naibu.stdlib:naibu-koin:1.0-RC.5")
    implementation("naibu.stdlib:naibu-ktor-server:1.0-RC.5")

    /* kmongo - database connectivity */
    implementation("org.litote.kmongo:kmongo-coroutine:4.7.1")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.7.1")

    /* remi - storage handling library */
    implementation(platform("org.noelware.remi:remi-bom:0.4.1-beta"))

    implementation("org.noelware.remi:remi-core")
    implementation("org.noelware.remi:remi-support-fs")
    implementation("org.noelware.remi:remi-support-s3")

    /* ktor - server library */
    implementation(platform("io.ktor:ktor-bom:2.1.2"))

    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-cio")

    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-partial-content")
    implementation("io.ktor:ktor-server-sessions")
    implementation("io.ktor:ktor-server-content-negotiation")

    implementation("io.ktor:ktor-serialization-kotlinx-json")

    /* misc */
    implementation("ch.qos.logback:logback-classic:1.4.4") // slf4j implementation
    implementation("de.mkammerer:argon2-jvm-nolibs:2.11")  // password hashing
}

tasks {
    val writeVersion = create("writeVersion") {
        file("src/main/resources/version.txt").writeText(version.toString())
    }

    withType<Jar> {
        dependsOn(writeVersion)
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
    }
}
