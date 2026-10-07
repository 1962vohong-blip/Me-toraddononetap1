package com.spearcheat.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels the velocity that vanilla spear charge would apply to the player.
 *
 * In 1.21.11, KineticWeaponComponent.onChargeRelease() adds velocity to the
 * player in the direction they're looking. We intercept setVelocity right
 * after any spear-related item usage ends and zero it out if the player
 * was holding a charged spear.
 *
 * Belt + suspenders: PlayerAttackMixin also zeroes velocity on hit.
 * This catches the case where the charge is released WITHOUT a hit target
 * (player missed) so they don't go flying anyway.
 */
@Mixin(PlayerEntity.class)
public abstract class SpearLaunchCancelMixin {

    @Inject(method = "onStoppedUsingItem", at = @At("RETURN"))
    private void cancelSpearLaunch(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        ItemStack active = self.getActiveItem();
        if (!isSpear(active)) return;

        // If we were charged, eat the velocity the game just gave us
        if (SpearChargeTrackerMixin.isSpearCharged(self)) {
            self.setVelocity(
                self.getVelocity().x,
                self.getVelocity().y,  // preserve Y so gravity still works
                self.getVelocity().z
            );
            // Zero out X/Z launch component — keep Y so no floating
            Vec3d current = self.getVelocity();
            self.setVelocity(0, current.y, 0);
            self.velocityModified = true;
        }
    }

    @Unique
    private boolean isSpear(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        String name = stack.getItem().getClass().getSimpleName();
        return name.contains("Spear") || name.contains("spear");
    }
}
