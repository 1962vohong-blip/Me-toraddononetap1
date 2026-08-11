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
    // 1. Minecraft
    minecraft("com.mojang:minecraft:1.21.11")
    
    // 2. Mappings (Chính xác cho 1.21.11)
    mappings("net.fabricmc:yarn:1.21.11+build.1:v2")
    
    // 3. Fabric Loader
    modImplementation("net.fabricmc:fabric-loader:0.16.5")
    
    // 4. Fabric API (Đã sửa đúng Group ID)
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.108.0+1.21.11")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
