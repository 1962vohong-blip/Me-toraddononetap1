package com.example.addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandExample {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommand(dispatcher);
        });
    }

    private static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("example")
                .executes(context -> {
                    context.getSource().sendFeedback(
                        () -> Text.literal("Example command executed!"),
                        false
                    );
                    return 1;
                })
        );
    }
}
