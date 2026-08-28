package com.example.customweapon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerTickCallback;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomWeaponMod implements ModInitializer {
    
    // Mod identifier used for logging and resource loading
    public static final String MOD_ID = "customweapon";
    
    // Logger for debug output
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // Global config manager - loads weapon settings from file
    public static final ConfigManager CONFIG = new ConfigManager();
    
    // Global keybind manager - handles RIGHT_SHIFT input
    public static final KeybindManager KEYBIND = new KeybindManager();
    
    /**
     * Initialize mod on startup
     * Registers all event listeners and loads configuration
     */
    @Override
    public void onInitialize() {
        // Load weapon configuration from config.json file
        CONFIG.loadConfig();
        
        // Register entity attack event listener
        // Fired whenever player attacks an entity
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            // Process weapon damage and abilities
            WeaponHandler.onAttackEntity(player, entity);
            // Return PASS to allow vanilla damage to occur as well
            return ActionResult.PASS;
        });
        
        // Register player tick event listener
        // Fired every game tick for each player
        PlayerTickCallback.EVENT.register(player -> {
            // Check for keybind input (RIGHT_SHIFT for menu)
            KeybindManager.handleKeybind(player);
        });
        
        LOGGER.info("Custom Weapon Mod Initialized - Sword Boost + Mace Slam!");
    }
}
