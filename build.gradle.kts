plugins {
    kotlin("jvm") version "2.2.20"

    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeHotReload)

    id("com.github.johnrengelman.shadow") version "8.1.1"
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
    implementation("org.slf4j:slf4j-simple:2.0.9")


    implementation("org.jetbrains.jediterm:jediterm-ui:3.59")
//    implementation("org.jetbrains.jediterm:jediterm-core:3.59")
    implementation("org.jetbrains.jediterm:jediterm-pty:2.69")
//    implementation("org.jetbrains.jediterm:jediterm-core-pty:3.0")
    implementation("org.jetbrains.pty4j:pty4j:0.13.11")

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
    // 1. Тепер ми залежимо від shadowJar, бо саме він містить ваші 5 бібліотек
    dependsOn("shadowJar")

    val pluginOutputDir = file("C:\\Users\\Anastasiia\\workspaces\\chaosnote\\chaosnote-desktop\\composeApp\\plugins")

    // 2. Беремо файл саме з таска shadowJar
    from(tasks.named("shadowJar"))

    into(pluginOutputDir)

    // 3. Перейменовуємо
    rename { "${project.name}-${project.version}.jar" }
}

// Залишаємо автоматичний запуск після build
tasks.named("build") {
    finalizedBy(copyPluginJar)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependencies {
        include(dependency("org.jetbrains.jediterm:.*:.*"))
        include(dependency("org.jetbrains.pty4j:.*:.*"))

        // ДОДАЙТЕ ЦЕ: JNA необхідна для роботи pty4j на Windows/Linux/macOS
        include(dependency("net.java.dev.jna:jna:.*"))
        include(dependency("net.java.dev.jna:jna-platform:.*"))

        // Також pty4j часто потребує purejavacomm (залежить від версії)
        include(dependency("org.jetbrains:purejavacomm:.*"))
    }
    mergeServiceFiles()
}