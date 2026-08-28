// src/main/java/com/example/customweapon/ConfigManager.java
// Configuration file management

package com.example.customweapon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Config Manager - Handles JSON configuration I/O
 * 
 * Manages weapon parameters:
 * - Sword damage multiplier
 * - Mace damage level
 * 
 * File location: config/customweapon/config.json
 */
public class ConfigManager {
    
    // GSON instance for JSON serialization
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();
    
    // Config file path
    private static final String CONFIG_DIR = "config/customweapon";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.json";
    
    /**
     * Configuration data class
     * Represents all weapon tunable parameters
     */
    public static class Config {
        // Sword damage multiplier (e.g., 1.5 = +50% damage)
        public double sword_multiplier = 1.5;
        
        // Mace slam damage (1-100 scale)
        public double mace_damage = 50.0;
    }
    
    // Current loaded configuration
    private Config config = new Config();
    
    /**
     * Load configuration from file
     * Creates default if file doesn't exist
     * Handles IO errors gracefully
     */
    public void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        
        try {
            // Create directory if doesn't exist
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
                CustomWeaponMod.LOGGER.info("Created config directory: " + CONFIG_DIR);
            }
            
            // Load from file if exists
            if (configFile.exists()) {
                try (FileReader reader = new FileReader(configFile)) {
                    Config loadedConfig = GSON.fromJson(reader, Config.class);
                    if (loadedConfig != null) {
                        config = loadedConfig;
                        CustomWeaponMod.LOGGER.info("Loaded configuration from " + CONFIG_FILE);
                    }
                }
            } else {
                // Create default config
                saveConfig();
                CustomWeaponMod.LOGGER.info("Created default configuration at " + CONFIG_FILE);
            }
            
        } catch (IOException e) {
            CustomWeaponMod.LOGGER.error("Error loading config: " + e.getMessage());
            CustomWeaponMod.LOGGER.warn("Using default configuration values");
        }
    }
    
    /**
     * Save current configuration to file
     * Overwrites existing file
     */
    public void saveConfig() {
        File configFile = new File(CONFIG_FILE);
        
        try {
            // Create directory if needed
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // Write config to file
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(config, writer);
            }
            
            CustomWeaponMod.LOGGER.info("Saved configuration to " + CONFIG_FILE);
            
        } catch (IOException e) {
            CustomWeaponMod.LOGGER.error("Error saving config: " + e.getMessage());
        }
    }
    
    /**
     * Get sword damage multiplier
     * 
     * @return sword multiplier (minimum 1.0)
     */
    public double getSwordMultiplier() {
        return Math.max(1.0, config.sword_multiplier);
    }
    
    /**
     * Get mace damage level
     * 
     * @return mace damage (1-100 range)
     */
    public double getMaceDamage() {
        return Math.max(1.0, Math.min(100.0, config.mace_damage));
    }
    
    /**
     * Set sword damage multiplier
     * Saves to file immediately
     *
     * @param value New multiplier value
     */
    public void setSwordMultiplier(double value) {
        config.sword_multiplier = Math.max(1.0, value);
        saveConfig();
        CustomWeaponMod.LOGGER.info("Sword multiplier set to: " + value);
    }
    
    /**
     * Set mace damage level
     * Saves to file immediately
     * Value clamped to 1-100
     *
     * @param value New damage level
     */
    public void setMaceDamage(double value) {
        config.mace_damage = Math.max(1.0, Math.min(100.0, value));
        saveConfig();
        CustomWeaponMod.LOGGER.info("Mace damage set to: " + config.mace_damage);
    }
}
