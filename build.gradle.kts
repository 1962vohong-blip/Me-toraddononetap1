plugins {
    id("net.fabricmc.fabric-loom") version "1.14.3"
}

version = "1.0.0"
group = "com.example.mod"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    "minecraft"("com.mojang:minecraft:1.21.11")
    
    // Sử dụng add() để tránh lỗi xung đột cấu hình trên Gradle mới
    add("mappings", "net.fabricmc:yarn:1.21.11+build.1:v2")
    
    "modImplementation"("net.fabricmc:fabric-loader:0.16.5")
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:0.108.0+1.21.11")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
