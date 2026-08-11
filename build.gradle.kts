plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.14.3"
}

version = "1.0.0"
group = "com.example.mod"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.meteordev.com/")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11")
    mappings("net.fabricmc:yarn:1.21.11+build.1:v2")
    
    modImplementation("net.fabricmc:fabric-loader:0.16.5")
    modImplementation("net.fabricmc:fabric-api:fabric-api:0.108.0+1.21.11")
    modImplementation("meteordevelopment:meteor-client:0.5.8")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
