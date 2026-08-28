// src/main/java/com/example/customweapon/CustomWeaponMod.java
// Main mod initialization

package com.example.customweapon;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerTickCallback;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Weapon Mod - Main Entry Point
 * 
 * Implements custom weapon mechanics for Minecraft 1.21:
 * - Sword: Passive damage multiplier (configurable 1.0+)
 * - Mace: Active ability - 2-tick launch + slam + AOE damage
 * 
 * All systems initialized on game startup
 */
public class CustomWeaponMod implements ModInitializer {
    
    // Mod identifier for resource loading and logging
    public static final String MOD_ID = "customweapon";
    
    // Logger instance
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // Global config manager - singleton pattern
    public static final ConfigManager CONFIG = new ConfigManager();
    
    // Global keybind manager - singleton pattern
    public static final KeybindManager KEYBIND = new KeybindManager();
    
    /**
     * Initialize mod on game startup
     * Called by Fabric once during game initialization
     * 
     * Registers event handlers and loads configuration
     */
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Custom Weapon Mod v1.21...");
        
        // Load configuration from file
        CONFIG.loadConfig();
        LOGGER.info("Configuration loaded from file");
        
        // Register attack event handler
        // Triggered when player attacks an entity
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            // Process weapon damage and abilities
            if (!world.isClient) {
                WeaponHandler.onAttackEntity(player, entity);
            }
            return ActionResult.PASS;
        });
        LOGGER.info("Attack event handler registered");
        
        // Register player tick event handler
        // Triggered every game tick for each player
        PlayerTickCallback.EVENT.register(player -> {
            if (!player.getWorld().isClient) {
                KeybindManager.handleKeybind(player);
            }
        });
        LOGGER.info("Tick event handler registered");
        
        LOGGER.info("Custom Weapon Mod initialization complete!");
    }
}
