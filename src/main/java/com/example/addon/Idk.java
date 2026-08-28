package com.example.customweapon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.List;

public class WeaponHandler {
    
    /**
     * Handle player attack entity event
     * Applies sword damage multiplier and triggers mace ability
     * 
     * @param player - The attacking player
     * @param target - The entity being attacked
     */
    public static void onAttackEntity(PlayerEntity player, Entity target) {
        ItemStack weaponStack = player.getMainHandStack();
        World world = player.getWorld();
        
        // === SWORD DAMAGE BOOST (PASSIVE) ===
        // Sword applies continuous damage multiplier (default 1.5x)
        if (isSword(weaponStack)) {
            double swordMultiplier = CustomWeaponMod.CONFIG.getSwordMultiplier();
            
            // Base sword damage = 6.0 (vanilla minecraft)
            double baseDamage = 6.0;
            double bonusDamage = baseDamage * (swordMultiplier - 1.0);
            
            // Apply bonus damage on top of base attack
            target.damage(
                new DamageSource(world.damageSources().playerAttack(player)), 
                (float)bonusDamage
            );
        }
        
        // === MACE ABILITY (ACTIVE - 2 TICK SLAM) ===
        // Mace launches player upward, then crashes down within 2 ticks
        if (isMace(weaponStack)) {
            activateMaceSlam(player, world);
        }
    }
    
    /**
     * Check if weapon is a sword type
     * 
     * @param stack - ItemStack to check
     * @return true if stack is any type of sword
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
     * Check if weapon is a mace
     * 
     * @param stack - ItemStack to check
     * @return true if stack is mace
     */
    private static boolean isMace(ItemStack stack) {
        return stack.getItem() == Items.MACE;
    }
    
    /**
     * Mace ability: Launch upward + crash down in 2 ticks
     * Execution timeline:
     * Tick 0: Player shoots upward (Y velocity = 60)
     * Tick 1: Player velocity reversed downward (Y velocity = -80)
     * Tick 2: Impact damage applied if player on ground
     * 
     * @param player - Player using mace ability
     * @param world - The game world
     */
    private static void activateMaceSlam(PlayerEntity player, World world) {
        // Prevent ability spam - only one active mace slam per player
        if (MaceState.isActive(player)) return;
        
        // Mark player as in active mace slam state
        MaceState.setActive(player, true);
        
        // Get mace damage multiplier from config (1-100 scale)
        double maceDamage = CustomWeaponMod.CONFIG.getMaceDamage();
        
        // === TICK 0: LAUNCH UPWARD ===
        // Set velocity to rapid upward movement
        player.setVelocity(new Vec3d(0, 60, 0));
        player.velocityModified = true;
        
        // Schedule downward crash and impact detection
        // Run async to avoid blocking game thread
        new Thread(() -> {
            try {
                // === TICK 1: REVERSE TO DOWNWARD ===
                // Wait 1 server tick (~50ms at 20 TPS)
                Thread.sleep(50);
                
                // Reverse velocity - now crashing downward at high speed
                player.setVelocity(new Vec3d(0, -80, 0));
                player.velocityModified = true;
                
                // === TICK 2: IMPACT CHECK ===
                // Wait additional 2 ticks for player to hit ground
                Thread.sleep(100);
                
                // Execute impact damage if player touching ground
                if (player.isOnGround()) {
                    executeMaceImpact(player, world, maceDamage);
                }
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Execute mace impact damage when player lands
     * Damages all nearby entities within 10 block radius
     * Then teleports player upward to safety
     * 
     * @param player - Player who used mace
     * @param world - The game world
     * @param maceDamage - Damage multiplier (1-100)
     */
    private static void executeMaceImpact(PlayerEntity player, World world, double maceDamage) {
        // Impact radius in blocks
        double radius = 10.0;
        
        // Get all entities in impact zone (excluding the player)
        List<Entity> nearbyEntities = 
            world.getOtherEntities(player, player.getBoundingBox().expand(radius));
        
        // Apply damage to all living entities in radius
        for (Entity entity : nearbyEntities) {
            if (entity instanceof net.minecraft.entity.LivingEntity && !entity.equals(player)) {
                net.minecraft.entity.LivingEntity living = 
                    (net.minecraft.entity.LivingEntity) entity;
                
                // Impact damage = mace_damage * 2 (multiply for heavy impact)
                living.damage(
                    new DamageSource(world.damageSources().playerAttack(player)), 
                    (float)(maceDamage * 2)
                );
            }
        }
        
        // Teleport player upward 5 blocks to prevent falling damage
        player.teleport(player.getX(), player.getY() + 5, player.getZ());
        
        // Reset mace state - ability can be used again
        MaceState.setActive(player, false);
    }
}
