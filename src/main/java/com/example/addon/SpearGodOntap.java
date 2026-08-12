package com.doomclient.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import java.util.*;
import java.util.stream.Collectors;

public class KillAuraModule extends Module {
    
    private double range = 3.0;
    private int maxCPS = 9;
    private int attackDelay = 8;
    private boolean autoCrit = true;
    private boolean lookAtTarget = true;
    private boolean playersOnly = true;
    
    private int tickCounter = 0;
    private long lastAttackTime = 0;
    
    public KillAuraModule() {
        super("KillAura", "Auto attack entities (CPS 12 - Reach 4.2 - Safe)", Category.COMBAT, GLFW.GLFW_KEY_R);
    }
    
    @Override
    public void onEnable() {
        tickCounter = 0;
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.of(
                "§a[KillAura] §fCPS: §e" + maxCPS + " §f| Reach: §e" + range + " §f| Safe mode"
            ), true);
        }
    }
    
    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.of("§c[KillAura] §fDisabled"), true);
        }
    }
    
    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        tickCounter++;
        if (tickCounter < attackDelay) return;
        tickCounter = 0;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime < (1000 / maxCPS)) return;
        
        Entity target = findNearestTarget();
        if (target == null) return;
        
        if (lookAtTarget) {
            Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2.0, 0);
            Vec3d playerPos = mc.player.getEyePos();
            double dx = targetPos.x - playerPos.x;
            double dy = targetPos.y - playerPos.y;
            double dz = targetPos.z - playerPos.z;
            double horizontalDist = Math.sqrt(dx * dx + dz * dz);
            
            mc.player.setYaw((float) Math.toDegrees(Math.atan2(-dx, dz)));
            mc.player.setPitch((float) Math.toDegrees(-Math.atan2(dy, horizontalDist)));
        }
        
        if (autoCrit && mc.player.isOnGround()) {
            mc.player.jump();
        }
        
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
        
        lastAttackTime = currentTime;
    }
    
    private Entity findNearestTarget() {
        return mc.world.getEntities().stream()
            .filter(e -> e != mc.player)
            .filter(e -> e instanceof LivingEntity)
            .filter(e -> e.isAlive())
            .filter(e -> mc.player.distanceTo(e) <= range)
            .filter(e -> playersOnly ? e instanceof PlayerEntity : true)
            .filter(e -> !(e instanceof PlayerEntity) || 
                !((PlayerEntity)e).getGameProfile().getName()
                    .equalsIgnoreCase(mc.player.getGameProfile().getName()))
            .sorted(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
            .collect(Collectors.toList())
            .stream()
            .findFirst()
            .orElse(null);
    }
    
    public void setRange(double range) { this.range = Math.min(6.0, Math.max(3.0, range)); }
    public double getRange() { return range; }
    public void setMaxCPS(int cps) { this.maxCPS = Math.min(20, Math.max(5, cps)); }
    public int getMaxCPS() { return maxCPS; }
}
