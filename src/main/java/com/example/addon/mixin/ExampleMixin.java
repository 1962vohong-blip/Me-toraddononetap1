// src/main/java/com/example/customweapon/command/WeaponCommand.java
// Command handler for /weapon command

package com.example.customweapon.command;

import com.example.customweapon.CustomWeaponMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * Weapon Command Handler
 * 
 * Registers /weapon command for changing weapon settings
 * Usage: /weapon sword <value>
 *        /weapon mace <1-100>
 */
public class WeaponCommand {
    
    /**
     * Register weapon command
     * Called during command registration phase
     */
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerWeaponCommand(dispatcher);
        });
    }
    
    /**
     * Register /weapon command with subcommands
     *
     * @param dispatcher Command dispatcher
     */
    private static void registerWeaponCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            CommandManager.literal("weapon")
                .then(
                    CommandManager.literal("sword")
                        .then(
                            CommandManager.argument("multiplier", 
                                DoubleArgumentType.doubleArg(1.0, 10.0))
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    double value = DoubleArgumentType.getDouble(context, "multiplier");
                                    
                                    CustomWeaponMod.CONFIG.setSwordMultiplier(value);
                                    source.sendFeedback(
                                        () -> Text.literal("§aSet sword multiplier to: " + value),
                                        true
                                    );
                                    return 1;
                                })
                        )
                )
                .then(
                    CommandManager.literal("mace")
                        .then(
                            CommandManager.argument("damage", 
                                DoubleArgumentType.doubleArg(1.0, 100.0))
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    double value = DoubleArgumentType.getDouble(context, "damage");
                                    
                                    CustomWeaponMod.CONFIG.setMaceDamage(value);
                                    source.sendFeedback(
                                        () -> Text.literal("§aSet mace damage to: " + value),
                                        true
                                    );
                                    return 1;
                                })
                        )
                )
        );
    }
}
