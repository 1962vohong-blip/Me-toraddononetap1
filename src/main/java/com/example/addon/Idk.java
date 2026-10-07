package com.spearcheat.mixin;

import com.spearcheat.SpearCheatMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerAttackMixin {

    // ──────────────────────────────────────────────────────────────────────────
    // Intercept PlayerEntity.attack(Entity target)
    // Fires whenever the player swings at something.
    // We check: holding a spear AND charge >= 1.5s → override everything.
    // ──────────────────────────────────────────────────────────────────────────
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        ItemStack held = self.getMainHandStack();

        // Only fire for spear with active charge
        if (!SpearChargeTrackerMixin.isSpearCharged(self)) return;
        if (!isSpear(held)) return;

        // Cancel the vanilla attack entirely — we handle everything
        ci.cancel();

        if (!(target instanceof LivingEntity victim)) return;

        World world = self.getWorld();

        // ── 1. Keep player FROZEN in place ────────────────────────────────────
        // Zero out any velocity the charge would have applied
        self.setVelocity(Vec3d.ZERO);
        self.velocityModified = true;

        // ── 2. Deal 1000 flat damage ──────────────────────────────────────────
        DamageSource src = world.getDamageSources().playerAttack(self);
        boolean hit = victim.damage(src, SpearCheatMod.OVERRIDE_DAMAGE);

        if (hit) {
            // ── 3. Strip ALL armor on the target ─────────────────────────────
            stripArmor(victim);

            // ── 4. Visual / audio feedback ───────────────────────────────────
            world.playSound(
                null,
                victim.getX(), victim.getY(), victim.getZ(),
                SoundEvents.ITEM_TRIDENT_HIT,      // closest vanilla sound
                SoundCategory.PLAYERS,
                1.5f, 0.8f
            );

            // Subtle knockback — target gets pushed slightly away from player
            // (not the player launching INTO target — just a small shove out)
            Vec3d knockDir = victim.getPos().subtract(self.getPos()).normalize();
            victim.addVelocity(knockDir.x * 0.4, 0.2, knockDir.z * 0.4);
            victim.velocityModified = true;
        }

        // Reset charge tracker so next swing is fresh
        SpearChargeTrackerMixin.resetCharge(self);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Strip every equipped armor piece to 1hp remaining durability,
    // then one more damage tick will break it naturally.
    // Using setDamage(maxDamage - 1) so the game's own break logic fires.
    // ──────────────────────────────────────────────────────────────────────────
    @Unique
    private void stripArmor(LivingEntity victim) {
        // Slot indices: 36 = boots, 37 = leggings, 38 = chestplate, 39 = helmet
        for (int slot = 36; slot <= 39; slot++) {
            ItemStack armor = victim.getInventory().getStack(slot);
            if (armor.isEmpty()) continue;

            int maxDur = armor.getMaxDamage();
            if (maxDur <= 0) continue; // unbreakable item (creative / custom)

            // Set damage to maxDamage - 1 → one hit away from breaking
            // The victim's next attack resolution will shatter it
            armor.setDamage(maxDur - 1);

            // Force-apply that final durability hit right now
            // damageable item uses component system in 1.21+
            armor.damage(1, victim, victim.getPreferredEquipmentSlot(armor));
        }
    }

    @Unique
    private boolean isSpear(ItemStack stack) {
        // In 1.21.11 the spear is net.minecraft.item.SpearItem
        // Check by class name for resilience against obfuscation variants
        String className = stack.getItem().getClass().getSimpleName();
        return className.contains("Spear") || className.contains("spear");
    }
}
