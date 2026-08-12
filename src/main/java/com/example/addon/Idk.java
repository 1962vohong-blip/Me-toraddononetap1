package com.doomclient.modules;

import com.doomclient.DoomClientMod;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class SpeedModule extends Module {
    
    private double speedMultiplier = 1.3;
    private boolean autoLimit = true;
    
    private static final double VANILLA_LIMIT = 3.0;
    private static final double NCP_LIMIT = 2.0;
    private static final double MLSAC1_LIMIT = 2.0;
    private static final double MLSAC2_LIMIT = 1.8;
    private static final double MLSAC_ADV_LIMIT = 1.5;
    private static final double FAST_PRO_LIMIT = 1.2;
    private static final double WATCHDOG_LIMIT = 1.8;
    private static final double VULCAN_LIMIT = 1.8;
    private static final double VERUS_LIMIT = 1.7;
    private static final double GRIMAC_LIMIT = 1.5;
    
    public SpeedModule() {
        super("Speed", "Increase movement speed (Default 1.3x - Safe)", Category.MOVEMENT, GLFW.GLFW_KEY_G);
    }
    
    @Override
    public void onEnable() {
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.of(
                "§a[Speed] §fSpeed set to §e" + speedMultiplier + "x"
            ), true);
            if (autoLimit) applyAutoLimit();
        }
    }
    
    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.of("§c[Speed] §fDisabled"), true);
        }
    }
    
    @Override
    public void onTick() {
        if (mc.player == null || !mc.player.isOnGround()) return;
        if (!mc.player.isSprinting() && !mc.options.forwardKey.isPressed()) return;
        
        Vec3d velocity = mc.player.getVelocity();
        double yaw = Math.toRadians(mc.player.getYaw());
        double forward = mc.player.input.movementForward;
        double sideways = mc.player.input.movementSideways;
        
        double targetX = -Math.sin(yaw) * forward + Math.cos(yaw) * sideways;
        double targetZ = Math.cos(yaw) * forward + Math.sin(yaw) * sideways;
        
        double currentSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        double targetSpeed = 0.13 * getEffectiveSpeed();
        
        if (currentSpeed < targetSpeed) {
            mc.player.setVelocity(targetX * targetSpeed, velocity.y, targetZ * targetSpeed);
        }
    }
    
    private double getEffectiveSpeed() {
        if (!autoLimit) return speedMultiplier;
        
        MLSACFastProBypass fastPro = (MLSACFastProBypass) DoomClientMod.INSTANCE.getModuleByName("MLSACFastPro");
        MLSACBypassModule mlsac = (MLSACBypassModule) DoomClientMod.INSTANCE.getModuleByName("MLSACBypass");
        
        if (fastPro != null && fastPro.isEnabled()) {
            return Math.min(speedMultiplier, FAST_PRO_LIMIT);
        }
        
        if (mlsac != null && mlsac.isEnabled() && mlsac.isMLSACDetected()) {
            int version = mlsac.getMLSACVersion();
            double limit = switch (version) {
                case 1 -> MLSAC1_LIMIT;
                case 2 -> MLSAC2_LIMIT;
                case 3 -> MLSAC_ADV_LIMIT;
                default -> NCP_LIMIT;
            };
            return Math.min(speedMultiplier, limit
