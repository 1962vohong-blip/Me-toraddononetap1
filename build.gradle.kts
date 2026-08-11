plugins {
    alias(libs.plugins.fabric.loom)
}

version = "1.0.0"
group = "com.example.mod"

repositories {
    mavenCentral()
    maven("https://maven.meteordev.com/")
}

dependencies {
    minecraft(libs.minecraft)
    mappings("net.fabricmc:yarn:1.21.11+build.1:v2")
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.meteor.client)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version))
    }
}
