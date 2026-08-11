plugins {
    id("fabric-loom") version "1.7.4"
    `maven-publish`
}

version = "1.0.0"
group = "com.example.mod"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings("net.fabricmc:yarn:1.21.4+build.8:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.9")
    
    // Sử dụng cú pháp này để tránh lỗi "Could not find"
    modImplementation(fabricApi.module("net.fabricmc.fabric-api:fabric-api", "0.119.2+1.21.4"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
