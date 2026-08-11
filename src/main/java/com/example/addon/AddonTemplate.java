package com.example.addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import static net.minecraft.server.command.CommandManager.literal;

public class CommandExample {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("onetap")
            .executes(context -> {
                // Nội dung lệnh của bạn ở đây
                return 1;
            })
        );
    }
}
