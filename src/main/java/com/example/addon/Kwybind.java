package com.spearcheat.mixin;

import com.spearcheat.SpearCheatMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(PlayerEntity.class)
public abstract class SpearChargeTrackerMixin {

    // ──────────────────────────────────────────────────────────────────────────
    // Static map: player UUID → tick when spear charge STARTED
    // ConcurrentHashMap because server may touch this from async threads
    // ──────────────────────────────────────────────────────────────────────────
    @Unique
    private static final Map<UUID, Long> chargeStartTick = new ConcurrentHashMap<>();

    // ──────────────────────────────────────────────────────────────────────────
    // Called every server tick while player is using an item.
    // usageTick fires when getItemUseTime > 0 → player is actively charging.
    // We record the FIRST tick of charge for this player.
    // ──────────────────────────────────────────────────────────────────────────
    @Inject(method = "tickItemStackUsage", at = @At("HEAD"))
    private void onTickItemUsage(ItemStack stack, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        if (!isSpear(stack)) {
            // Not a spear — clear any stale record
            chargeStartTick.remove(self.getUuid());
            return;
        }

        // Record start tick only once per charge session
        chargeStartTick.putIfAbsent(
            self.getUuid(),
            self.getWorld().getTime()
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // When player stops using item (releases right-click / charge),
    // remove the charge record regardless of outcome.
    // ──────────────────────────────────────────────────────────────────────────
    @Inject(method = "clearActiveItem", at = @At("HEAD"))
    private void onClearActiveItem(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        chargeStartTick.remove(self.getUuid());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Public API — queried by PlayerAttackMixin
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Returns true if this player has held a spear charge for >= 1.5 seconds
     * (CHARGE_TICKS_REQUIRED ticks at 20tps).
     */
    @Unique
    public static boolean isSpearCharged(PlayerEntity player) {
        Long startTick = chargeStartTick.get(player.getUuid());
        if (startTick == null) return false;

        long elapsed = player.getWorld().getTime() - startTick;
        return elapsed >= SpearCheatMod.CHARGE_TICKS_REQUIRED;
    }

    /**
     * Clear the charge record after a successful override hit.
     */
    @Unique
    public static void resetCharge(PlayerEntity player) {
        chargeStartTick.remove(player.getUuid());
    }

    @Unique
    private boolean isSpear(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String name = stack.getItem().getClass().getSimpleName();
        return name.contains("Spear") || name.contains("spear");
    }
}
