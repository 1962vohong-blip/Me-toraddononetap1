// src/main/java/com/example/customweapon/WeaponHandler.java
// Core weapon mechanics

package com.example.customweapon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.List;

/**
 * Weapon Handler - Processes all weapon damage and abilities
 * 
 * Handles:
 * - Sword damage multiplier application
 * - Mace special ability (2-tick slam)
 */
public class WeaponHandler {
    
    /**
     * Main attack handler
     * Called when player attacks entity
     * Determines weapon type and applies effects
     *
     * @param player The attacking player
     * @param target The entity being attacked
     */
    public static void onAttackEntity(PlayerEntity player, Entity target) {
        // Get weapon from main hand
        ItemStack weaponStack = player.getMainHandStack();
        World world = player.getWorld();
        
        // Skip if player is on client side
        if (world.isClient) return;
        
        // === SWORD: PASSIVE DAMAGE BOOST ===
        if (isSword(weaponStack)) {
            applySwordDamage(player, target);
        }
        
        // === MACE: ACTIVE 2-TICK SLAM ===
        if (isMace(weaponStack)) {
            activateMaceAbility(player, world);
        }
    }
    
    /**
     * Apply sword damage multiplier to target
     * Bonus damage = base damage × (multiplier - 1)
     *
     * @param player Attacking player
     * @param target Target entity
     */
    private static void applySwordDamage(PlayerEntity player, Entity target) {
        // Get multiplier from config
        double multiplier = CustomWeaponMod.CONFIG.getSwordMultiplier();
        
        // Base sword damage (vanilla)
        double baseDamage = 6.0;
        
        // Calculate bonus damage
        double bonusDamage = baseDamage * (multiplier - 1.0);
        
        // Apply bonus damage if multiplier > 1.0
        if (bonusDamage > 0.0) {
            target.damage(
                player.getDamageSources().playerAttack(player),
                (float) bonusDamage
            );
            
            CustomWeaponMod.LOGGER.debug(
                "Sword damage applied: " + bonusDamage + " (multiplier: " + multiplier + ")"
            );
        }
    }
    
    /**
     * Activate mace slam ability
     * Timeline:
     * - Tick 0: Launch player upward (Y velocity = 60)
     * - Tick 1: Reverse velocity downward (Y velocity = -80)
     * - Tick 2: Impact damage on ground collision
     *
     * @param player Player using mace
     * @param world Game world
     */
    private static void activateMaceAbility(PlayerEntity player, World world) {
        // Check if already in slam animation
        if (MaceState.isActive(player)) {
            return;
        }
        
        // Mark player as active
        MaceState.setActive(player, true);
        
        // Get mace damage from config (1-100)
        double maceDamage = CustomWeaponMod.CONFIG.getMaceDamage();
        
        // === TICK 0: LAUNCH UPWARD ===
        player.setVelocity(new Vec3d(0, 60, 0));
        player.velocityModified = true;
        
        CustomWeaponMod.LOGGER.debug("Mace slam initiated - launching player upward");
        
        // Schedule slam sequence on separate thread
        Thread slamThread = new Thread(() -> {
            try {
                // === TICK 1: DELAY 1 TICK (~50MS AT 20 TPS) ===
                Thread.sleep(50);
                
                // Reverse velocity to downward crash
                player.setVelocity(new Vec3d(0, -80, 0));
                player.velocityModified = true;
                
                CustomWeaponMod.LOGGER.debug("Mace slam - reversing to downward velocity");
                
                // === TICK 2: DELAY 2 MORE TICKS (~100MS) ===
                Thread.sleep(100);
                
                // Check if player on ground and execute impact
                if (player.isOnGround() && !world.isClient) {
                    executeMaceImpact(player, world, maceDamage);
                }
                
            } catch (InterruptedException e) {
                CustomWeaponMod.LOGGER.error("Mace slam thread interrupted: " + e.getMessage());
            }
        });
        
        slamThread.setName("CustomWeapon-MaceSlamThread");
        slamThread.start();
    }
    
    /**
     * Execute mace impact damage
     * Damages all entities in 10 block radius
     * Teleports player upward to safety
     *
     * @param player Player who slammed
     * @param world Game world
     * @param maceDamage Damage multiplier (1-100)
     */
    private static void executeMaceImpact(PlayerEntity player, World world, double maceDamage) {
        // Impact radius in blocks
        double radius = 10.0;
        
        // Get all entities in impact zone
        List<Entity> nearbyEntities = world.getOtherEntities(
            player,
            player.getBoundingBox().expand(radius)
        );
        
        CustomWeaponMod.LOGGER.debug("Mace impact: " + nearbyEntities.size() + " entities in radius");
        
        // Apply damage to each entity
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity && !entity.equals(player)) {
                LivingEntity living = (LivingEntity) entity;
                
                // Impact damage = mace_damage × 2
                float damageAmount = (float) (maceDamage * 2);
                
                living.damage(
                    player.getDamageSources().playerAttack(player),
                    damageAmount
                );
            }
        }
        
        // Teleport player upward 5 blocks
        double newX = player.getX();
        double newY = player.getY() + 5;
        double newZ = player.getZ();
        
        player.teleport(newX, newY, newZ);
        
        CustomWeaponMod.LOGGER.debug("Mace impact executed - player teleported to safety");
        
        // Reset mace state
        MaceState.setActive(player, false);
    }
    
    /**
     * Check if item is a sword
     *
     * @param stack ItemStack to check
     * @return true if sword type
     */
    private static boolean isSword(ItemStack stack) {
        return stack.getItem() == Items.DIAMOND_SWORD ||
               stack.getItem() == Items.IRON_SWORD ||
               stack.getItem() == Items.STONE_SWORD ||
               stack.getItem() == Items.WOODEN_SWORD ||
               stack.getItem() == Items.GOLDEN_SWORD ||
               stack.getItem() == Items.NETHERITE_SWORD;
    }
    
    /**
     * Check if item is a mace
     *
     * @param stack ItemStack to check
     * @return true if mace
     */
    private static boolean isMace(ItemStack stack) {
        return stack.getItem() == Items.MACE;
    }
}
