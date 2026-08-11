plugins {
    id("net.fabricmc.fabric-loom") version "1.14.3"
}

version = "1.0.0"
group = "com.example.mod"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.meteordev.com/")
}

dependencies {
    add("minecraft", "com.mojang:minecraft:1.21.11")
    add("mappings", "net.fabricmc:yarn:1.21.11+build.1:v2")
    
    add("modImplementation", "net.fabricmc:fabric-loader:0.16.5")
    add("modImplementation", "net.fabricmc.fabric-api:fabric-api:0.108.0+1.21.11")
    add("modImplementation", "meteordevelopment:meteor-client:0.5.8")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
