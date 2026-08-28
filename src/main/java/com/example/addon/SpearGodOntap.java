// src/main/java/com/example/customweapon/MaceState.java
// Track mace ability state

package com.example.customweapon;

import net.minecraft.entity.player.PlayerEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Mace State Tracker
 * 
 * Tracks which players are currently performing mace slam
 * Prevents ability spam and concurrent slam executions
 */
public class MaceState {
    
    /**
     * Map of player UUID to slam state
     * Uses UUID instead of PlayerEntity to avoid holding references
     */
    private static final Map<UUID, Boolean> activeStates = new HashMap<>();
    
    /**
     * Check if player is currently in mace slam animation
     *
     * @param player Player to check
     * @return true if slam is active
     */
    public static boolean isActive(PlayerEntity player) {
        return activeStates.getOrDefault(player.getUuid(), false);
    }
    
    /**
     * Set mace slam state for player
     *
     * @param player Player to update
     * @param active true if starting slam, false if ending slam
     */
    public static void setActive(PlayerEntity player, boolean active) {
        activeStates.put(player.getUuid(), active);
    }
    
    /**
     * Remove player from tracking on disconnect
     * Prevents memory leaks from stale player references
     *
     * @param player Player disconnecting
     */
    public static void removePlayer(PlayerEntity player) {
        activeStates.remove(player.getUuid());
    }
    
    /**
     * Clear all state tracking
     * Called on server shutdown
     */
    public static void clearAll() {
        activeStates.clear();
    }
}
