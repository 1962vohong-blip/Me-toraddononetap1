// src/main/java/com/example/customweapon/mixin/PlayerDisconnectMixin.java
// Cleanup on player disconnect

package com.example.customweapon.mixin;

import com.example.customweapon.KeybindManager;
import com.example.customweapon.MaceState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.callback.CallbackInfo;

/**
 * Player Disconnect Mixin
 * 
 * Cleans up player data when disconnecting
 * Prevents memory leaks from stale player references
 */
@Mixin(PlayerEntity.class)
public class PlayerDisconnectMixin {
    
    /**
     * Called when player is removed from world
     * Cleanup tracking data
     */
    @Inject(method = "remove", at = @At("HEAD"))
    private void onPlayerRemove(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // Remove from mace state tracking
        MaceState.removePlayer(player);
        
        // Remove from keybind tracking
        KeybindManager.removePlayer(player);
    }
}
