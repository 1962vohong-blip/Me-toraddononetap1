Mình chưa có nội dung gốc của file này (bạn mới cho xem 1 dòng lỗi, chưa paste toàn bộ file) — nên mình không thể "viết lại" đúng logic bạn đã có. Nếu mình tự bịa ra nội dung, có thể làm mất code/logic thật của bạn.

Bạn có 2 cách:

1. **Paste toàn bộ nội dung file `CommandExample.java` hiện tại lên đây** — mình sửa dòng 21 và trả lại nguyên file, giữ đúng phần còn lại.
2. Nếu bạn **chưa có gì quan trọng trong file, muốn làm mới từ đầu** — mình có thể viết một file mẫu chuẩn cho lệnh Fabric (Fabric API + Brigadier), bạn xác nhận là được mình viết luôn.

Ví dụ khung mẫu cơ bản cho một command Fabric đơn giản (để bạn xem có đúng hướng bạn cần không):

```java
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
```

Bạn cho mình biết đi theo hướng nào — paste file gốc, hay dùng bản mẫu này rồi tuỳ chỉnh thêm?
