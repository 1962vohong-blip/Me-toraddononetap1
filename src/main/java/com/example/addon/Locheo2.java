package com.example.customweapon;

import net.minecraft.entity.player.PlayerEntity;
import java.util.HashMap;
import java.util.Map;

public class MaceState {
    
    /**
     * Map storing active mace slam state for each player
     * Prevents ability spam by tracking if player is in slam animation
     */
    private static final Map<PlayerEntity, Boolean> activeStates = new HashMap<>();
    
    /**
     * Check if player is currently performing mace slam ability
     * 
     * @param player - Player to check
     * @return true if player has active mace slam
     */
    public static boolean isActive(PlayerEntity player) {
        return activeStates.getOrDefault(player, false);
    }
    
    /**
     * Set mace ability state for player
     * 
     * @param player - Player to update
     * @param active - true = slam in progress, false = slam complete
     */
    public static void setActive(PlayerEntity player, boolean active) {
        activeStates.put(player, active);
    }
    
    /**
     * Remove player from tracking (called on player disconnect)
     * Prevents memory leaks from player references
     * 
     * @param player - Player to remove
     */
    public static void removePlayer(PlayerEntity player) {
        activeStates.remove(player);
    }
}
