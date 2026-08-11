package com.example.mod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class ExampleMod implements ModInitializer {
    private static final float CUSTOM_SWORD_DAMAGE = 50.0f; // Sát thương Kiếm (1-100)

    @Override
    public void onInitialize() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            // 1. Xử lý cận chiến (Kiếm, Chùy)
            if (source.getAttacker() instanceof ServerPlayerEntity player) {
                var mainHand = player.getMainHandStack();

                if (mainHand.getItem() instanceof SwordItem) {
                    amount = CUSTOM_SWORD_DAMAGE;
                }

                if (mainHand.isOf(Items.MACE)) {
                    breakArmor(entity);
                }
            }

            // 2. Xử lý tầm xa từ Cung (Bow Power Shot phá giáp)
            if (source.getSource() instanceof PersistentProjectileEntity arrow) {
                if (arrow.getOwner() instanceof ServerPlayerEntity) {
                    breakArmor(entity);
                }
            }

            return true;
        });
    }

    private static void breakArmor(LivingEntity entity) {
        for (var armorStack : entity.getArmorItems()) {
            if (!armorStack.isEmpty()) {
                armorStack.setDamage(armorStack.getMaxDamage());
            }
        }
        entity.getWorld().playSound(
            null, entity.getX(), entity.getY(), entity.getZ(),
            SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0f, 0.5f
        );
    }
}
