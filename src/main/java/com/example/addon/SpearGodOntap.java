package com.example.addon.modules;

import com.example.addon.ExampleAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class SpearGodModule extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("Tầm đánh của Spear (tối đa 100 blocks).")
        .defaultVal(100.0)
        .min(1.0)
        .max(100.0)
        .build()
    );

    public SpearGodModule() {
        super(ExampleAddon.CATEGORY, "spear-god", "One-tap mục tiêu trong phạm vi 100 blocks với Spear.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        // Kiểm tra xem người chơi có đang cầm vũ khí/Spear ở tay chính không
        boolean holdingSpear = mc.player.getMainHandStack().getItem().toString().contains("spear") 
            || mc.player.getMainHandStack().isOf(Items.WOODEN_SWORD); // Thay thế bằng item ID thực tế của Spear trong 1.21.11

        if (!holdingSpear) return;

        // Quét tìm entity sống trong bán kính 100 blocks
        Entity target = null;
        double minDst = range.get();

        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (entity instanceof LivingEntity && entity.isAlive() && !entity.isSpectator()) {
                double dist = mc.player.distanceTo(entity);
                if (dist <= minDst) {
                    minDst = dist;
                    target = entity;
                }
            }
        }

        // Tự động khóa mục tiêu và tấn công lập tức
        if (target != null) {
            Entity finalTarget = target;
            Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target), 100, () -> {
                mc.interactionManager.attackEntity(mc.player, finalTarget);
                mc.player.swingHand(Hand.MAIN_HAND);
            });
        }
    }
}
