import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application

    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
}

group   = "fairu.backend"
version = "2.0"

application {
    mainClass.set("fairu.backend.LauncherKt")
}

repositories {
    mavenCentral()
    maven("https://maven.dimensional.fun/releases")
    maven("https://jitpack.io")
}

dependencies {
    /* kotlin */
    implementation(kotlin("stdlib"))

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // logging
    implementation("io.github.microutils:kotlin-logging:3.0.5")

    /* scrimage - image generation */
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.33")

    /* koin - dependency injection */
    implementation("io.insert-koin:koin-core:3.3.3")

    /* toml - configuration format */
    implementation("com.akuleshov7:ktoml-core:0.4.1")
    implementation("com.akuleshov7:ktoml-file:0.4.1")

    /* naibu - utilities */
    implementation("naibu.stdlib:naibu-core:1.0-RC.14")
    implementation("naibu.stdlib:naibu-io:1.0-RC.14")

    // extensions
    implementation("naibu.stdlib:naibu-koin:1.0-RC.14")
    implementation("naibu.stdlib:naibu-ktor-server:1.0-RC.14")
    implementation("naibu.stdlib:naibu-scrimage:1.0-RC.14")

    /* kmongo - database connectivity */
    implementation("org.litote.kmongo:kmongo-coroutine:4.8.0")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.8.0")

    /* aws - s3 client */
    implementation("aws.sdk.kotlin:s3:0.17.5-beta")
    implementation("aws.smithy.kotlin:http-client-engine-ktor:0.13.1")

    /*  - content type checking */
    implementation("com.github.overview:mime-types:6e273e3")

    /* ktor - server library */
    implementation(platform("io.ktor:ktor-bom:2.2.4"))

    // client
    implementation("io.ktor:ktor-client-cio")

    // server
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-cio")

    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-partial-content")
    implementation("io.ktor:ktor-server-sessions")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-default-headers")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-cors")

    implementation("io.ktor:ktor-serialization-kotlinx-json")

    /* misc */
    implementation("ch.qos.logback:logback-classic:1.4.6") // slf4j implementation
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
