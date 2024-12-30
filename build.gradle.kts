plugins {
    kotlin("jvm") version "1.9.23"
}

group = "paket"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2") // For asynchronous support, if needed
    implementation("com.github.kittinunf.fuel:fuel:2.3.1") // Alternative HTTP client (optional)
    implementation("com.squareup.okhttp3:okhttp:4.11.0") // For HTTP requests
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2") // JSON parsing
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
