// src/main/java/com/example/customweapon/KeybindManager.java
// Handle keybind input

package com.example.customweapon;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Keybind Manager - Handles player input
 * 
 * Detects RIGHT_SHIFT (sneak) key press
 * Opens weapon configuration menu on key press
 */
public class KeybindManager {
    
    /**
     * Track previous frame SHIFT state per player
     * Uses UUID to identify players
     */
    private static final Map<UUID, Boolean> lastShiftState = new HashMap<>();
    
    /**
     * Handle player input each tick
     * Detects SHIFT key press and opens menu
     *
     * @param player Player to check input for
     */
    public static void handleKeybind(PlayerEntity player) {
        UUID playerId = player.getUuid();
        
        // Get current SHIFT state
        boolean isShifting = player.input != null && player.input.sneak;
        
        // Get previous state (default false)
        boolean wasShifting = lastShiftState.getOrDefault(playerId, false);
        
        // Trigger on rising edge (key pressed, wasn't pressed before)
        if (isShifting && !wasShifting) {
            openWeaponMenu(player);
        }
        
        // Update state for next frame
        lastShiftState.put(playerId, isShifting);
    }
    
    /**
     * Display weapon configuration menu to player
     * Shows current settings and command usage
     *
     * @param player Player to show menu to
     */
    private static void openWeaponMenu(PlayerEntity player) {
        // Get current configuration values
        double swordMult = CustomWeaponMod.CONFIG.getSwordMultiplier();
        double maceDmg = CustomWeaponMod.CONFIG.getMaceDamage();
        
        // Send menu header
        player.sendMessage(
            Text.literal("§6========== CUSTOM WEAPON MENU =========="),
            false
        );
        
        // Send current values
        player.sendMessage(
            Text.literal("§eSword Damage Multiplier: §a" + swordMult),
            false
        );
        player.sendMessage(
            Text.literal("§eMace Damage Level (1-100): §a" + maceDmg),
            false
        );
        
        // Send command usage
        player.sendMessage(
            Text.literal("§7"),
            false
        );
        player.sendMessage(
            Text.literal("§7§lUsage:"),
            false
        );
        player.sendMessage(
            Text.literal("§7  /weapon sword <multiplier>  (example: 2.0)"),
            false
        );
        player.sendMessage(
            Text.literal("§7  /weapon mace <damage>  (example: 75)"),
            false
        );
        
        // Send menu footer
        player.sendMessage(
            Text.literal("§6=========================================="),
            false
        );
    }
    
    /**
     * Remove player from tracking on disconnect
     *
     * @param player Disconnecting player
     */
    public static void removePlayer(PlayerEntity player) {
        lastShiftState.remove(player.getUuid());
    }
}
