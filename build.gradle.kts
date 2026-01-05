plugins {
    kotlin("jvm") version "2.2.20"

    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

group = "com.chaosnote"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    google()
    mavenCentral()
}

dependencies {
    implementation("com.chaosnote:api:1.0")

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.ui)
    implementation(compose.components.resources)
    implementation(compose.components.uiToolingPreview)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

val copyPluginJar by tasks.registering(Copy::class) {
    dependsOn("jar") // спершу збираємо JAR
    val pluginOutputDir = file("C:\\Users\\Anastasiia\\workspaces\\ChaosNote-Desktop\\composeApp\\plugins") // зміни на свою папку
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