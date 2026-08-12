package com.doomclient.modules;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import java.util.*;

public class MaceDamageModule extends Module {

    // === CONFIG ===
    private boolean destroyArmor = true;
    private double durabilityDamageMultiplier = 2.0;
    private double armorBreakChance = 1.0;
    
    private boolean breakHelmet = true;
    private boolean breakChestplate = true;
    private boolean breakLeggings = true;
    private boolean breakBoots = true;
    private boolean breakOffhand = false;
    
    private double maxRange = 3.0;
    private boolean autoSwitch = true;
    private boolean autoCrit = true;
    
    // === SMART MODE - WAIT FOR RED ARMOR ===
    private boolean smartMode = true;
    private boolean notifyArmorStatus = true;
    private double redThreshold = 0.25;
    
    private int tickCounter = 0;
    private int attackCounter = 0;
    private Entity currentTarget = null;
    
    public MaceDamageModule() {
        super("MaceDamage", "Break armor with Mace - Smart mode (wait for red armor)", Category.COMBAT, GLFW.GLFW_KEY_M);
    }

    @Override
    public void onEnable() {
        tickCounter = 0;
        attackCounter = 0;
        currentTarget = null;
        
        if (mc.player != null) {
            mc.player.sendMessage(Text.of(
                "§a[MaceDamage] §fMode: §e" + (smartMode ? "SMART (wait for red armor)" : "INSTANT")
            ), true);
            if (autoSwitch) switchToMace();
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.sendMessage(Text.of(
                "§c[MaceDamage] §fDisabled - §7Destroyed " + attackCounter + " armor pieces"
            ), true);
        }
        currentTarget = null;
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        tickCounter++;

        if (!isHoldingMace()) {
            if (autoSwitch) switchToMace();
            return;
        }

        Entity target = findNearestTarget();
        if (target == null) {
            if (smartMode && currentTarget != null && notifyArmorStatus) {
                checkArmorStatus(currentTarget);
            }
            return;
        }
        
        currentTarget = target;
        lookAtTarget(target);

        if (autoCrit && mc.player.isOnGround() && tickCounter >= 5) {
            mc.player.jump();
            tickCounter = 0;
        }

        if (mc.player.distanceTo(target) <= maxRange) {
            if (smartMode) {
                if (target instanceof LivingEntity living) {
                    if (isArmorRed(living)) {
                        if (notifyArmorStatus) {
                            mc.player.sendMessage(Text.of(
                                "§c[MaceDamage] §f§lARMOR RED! §fBreaking armor of §c" + 
                                target.getName().getString()
                            ), true);
                        }
                        attackTarget(target);
                    } else {
                        if (notifyArmorStatus && tickCounter % 40 == 0) {
                            double avgDurability = getAverageArmorDurability(living);
                            mc.player.sendMessage(Text.of(
                                "§7[MaceDamage] §fArmor of §e" + target.getName().getString() + 
                                " §fat §a" + (int)(avgDurability * 100) + "% §7- Waiting for red..."
                            ), true);
                        }
                    }
                }
            } else {
                attackTarget(target);
            }
        }
    }

    private void checkArmorStatus(Entity target) {
        if (target instanceof LivingEntity living && isArmorRed(living)) {
            mc.player.sendMessage(Text.of(
                "§c§l[MaceDamage] §fWARNING: Armor of §c" + target.getName().getString() + 
                " §fis RED! Enable MaceDamage now!"
            ), true);
        }
    }

    private boolean isArmorRed(LivingEntity entity) {
        double avgDurability = getAverageArmorDurability(entity);
        return avgDurability <= redThreshold;
    }

    private double getAverageArmorDurability(LivingEntity entity) {
        List<EquipmentSlot> slots = Arrays.asList(
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, 
            EquipmentSlot.LEGS, EquipmentSlot.FEET
        );
        
        double totalDurability = 0;
        int count = 0;
        
        for (EquipmentSlot slot : slots) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (!stack.isEmpty() && stack.getMaxDamage() > 0) {
                double remaining = 1.0 - (double)stack.getDamage() / stack.getMaxDamage();
                totalDurability += remaining;
                count++;
            }
        }
        
        return count > 0 ? totalDurability / count : 0;
    }

    private boolean isHoldingMace() {
        return mc.player.getMainHandStack().getItem() instanceof MaceItem;
    }

    private void switchToMace() {
        PlayerInventory inv = mc.player.getInventory();
        if (inv.getMainHandStack().getItem() instanceof MaceItem) return;
        
        for (int i = 0; i < 9; i++) {
            if (inv.getStack(i).getItem() instanceof MaceItem) {
                inv.selectedSlot = i;
                return;
            }
        }
        for (int i = 9; i < 36; i++) {
            if (inv.getStack(i).getItem() instanceof MaceItem) {
                mc.interactionManager.pickFromInventory(i);
                inv.selectedSlot = findEmptyHotbarSlot();
                return;
            }
        }
    }

    private int findEmptyHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) return i;
        }
        return 0;
    }

    private Entity findNearestTarget() {
        return mc.world.getEntities().stream()
            .filter(e -> e != mc.player)
            .filter(e -> e instanceof LivingEntity)
            .filter(e -> e.isAlive())
            .filter(e -> mc.player.distanceTo(e) <= maxRange)
            .filter(e -> !(e instanceof PlayerEntity) || 
                !((PlayerEntity)e).getGameProfile().getName()
                    .equalsIgnoreCase(mc.player.getGameProfile().getName()))
            .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
            .orElse(null);
    }

    private void lookAtTarget(Entity target) {
        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2.0, 0);
        Vec3d playerPos = mc.player.getEyePos();
        double dx = targetPos.x - playerPos.x;
        double dy = targetPos.y - playerPos.y;
        double dz = targetPos.z - playerPos.z;
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
        
        mc.player.setYaw((float) Math.toDegrees(Math.atan2(-dx, dz)));
        mc.player.setPitch((float) Math.toDegrees(-Math.atan2(dy, horizontalDist)));
    }

    private void attackTarget(Entity target) {
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        if (target instanceof LivingEntity livingTarget) {
            breakArmor(livingTarget);
        }
    }

    private void breakArmor(LivingEntity target) {
        if (Math.random() > armorBreakChance) return;

        List<EquipmentSlot> slotsToBreak = new ArrayList<>();
        if (breakHelmet && !target.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) 
            slotsToBreak.add(EquipmentSlot.HEAD);
        if (breakChestplate && !target.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) 
            slotsToBreak.add(EquipmentSlot.CHEST);
        if (breakLeggings && !target.getEquippedStack(EquipmentSlot.LEGS).isEmpty()) 
            slotsToBreak.add(EquipmentSlot.LEGS);
        if (breakBoots && !target.getEquippedStack(EquipmentSlot.FEET).isEmpty()) 
            slotsToBreak.add(EquipmentSlot.FEET);
        if (breakOffhand && !target.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty()) 
            slotsToBreak.add(EquipmentSlot.OFFHAND);

        for (EquipmentSlot slot : slotsToBreak) {
            ItemStack armorStack = target.getEquippedStack(slot);
            if (armorStack.isEmpty()) continue;

            if (destroyArmor) {
                target.equipStack(slot, ItemStack.EMPTY);
                target.getWorld().playSound(
                    null, target.getBlockPos(),
                    SoundEvents.ENTITY_ITEM_BREAK,
                    SoundCategory.PLAYERS, 1.0f, 1.0f
                );
                attackCounter++;
                if (mc.player != null) {
                    mc.player.sendMessage(Text.of(
                        "§c[MaceDamage] §fDestroyed §e" + armorStack.getName().getString() + 
                        " §fof §c" + target.getName().getString()
                    ), true);
                }
            } else {
                int newDamage = armorStack.getDamage() + 
                    (int)(armorStack.getMaxDamage() * 0.3 * durabilityDamageMultiplier);
                if (newDamage >= armorStack.getMaxDamage()) {
                    target.equipStack(slot, ItemStack.EMPTY);
                    target.getWorld().playSound(
                        null, target.getBlockPos(),
                        SoundEvents.ENTITY_ITEM_BREAK,
                        SoundCategory.PLAYERS, 1.0f, 1.0f
                    );
                    attackCounter++;
                } else {
                    armorStack.setDamage(newDamage);
                }
            }
        }
        // NO lightning, NO electric particles
    }

    // === GETTERS/SETTERS ===
    public void setSmartMode(boolean smart) { this.smartMode = smart; }
    public boolean isSmartMode() { return smartMode; }
    public void setDestroyArmor(boolean destroy) { this.destroyArmor = destroy; }
    public void setArmorBreakChance(double chance) { this.armorBreakChance = Math.min(1.0, Math.max(0.0, chance)); }
    public void setNotifyArmorStatus(boolean notify) { this.notifyArmorStatus = notify; }
}
