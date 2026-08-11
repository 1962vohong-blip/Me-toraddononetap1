package com.example.mod.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class SpearCombatMixin {

    // Nâng lượng sát thương lên đúng 10,000 khi tấn công bằng Spear
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float modifyDamageAmount(float amount, DamageSource source) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            if (player.getMainHandStack().getName().getString().toLowerCase().contains("spear")) {
                return 10000.0f;
            }
        }
        return amount;
    }

    // Triệt tiêu vận tốc, giữ nguyên 1 chỗ không bị đẩy lùi khi vung Spear
    @Inject(method = "damage", at = @At("HEAD"))
    private void freezePlayerOnAttack(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            if (player.getMainHandStack().getName().getString().toLowerCase().contains("spear")) {
                player.setVelocity(0, player.getVelocity().y, 0);
                player.velocityModified = true;
            }
        }
    }
}
