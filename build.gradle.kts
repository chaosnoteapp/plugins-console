plugins {
    kotlin("jvm") version "2.2.20"

    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeHotReload)
}

group = "com.chaosnote"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies/")
    google()
    mavenCentral()
}

dependencies {
    implementation("com.github.chaosnoteapp:chaosnote-api:v0.1.0")
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)
    implementation(compose.components.uiToolingPreview)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.0")

    implementation("org.jetbrains.jediterm:jediterm-ui:3.59")
    implementation("org.jetbrains.jediterm:jediterm-core:3.59")
    implementation("org.jetbrains.jediterm:jediterm-pty:2.69")
    implementation("org.jetbrains.jediterm:jediterm-core-pty:3.0")
    implementation("org.jetbrains.pty4j:pty4j:0.13.11")
    implementation("org.slf4j:slf4j-simple:2.0.9")

    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "com.chaosnote.plugin.MainKt"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe)
            packageName = "Chaosnote"
            packageVersion = "1.0.0"
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

val copyPluginJar by tasks.registering(Copy::class) {
    dependsOn("jar") // спершу збираємо JAR
    val pluginOutputDir = file("C:\\Users\\Anastasiia\\workspaces\\chaosnote\\chaosnote-desktop\\composeApp\\plugins") // зміни на свою папку
    from(tasks.named("jar")) {
        // джарка, яку згенерує task 'jar'
    }
    into(pluginOutputDir)
    rename { "${project.name}-${project.version}.jar" } // можна змінити ім'я
}

// Зробимо так, щоб copyPluginJar запускалось після build
tasks.named("build") {
    finalizedBy(copyPluginJar)
}