package com.example.mod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class ExampleModClient implements ClientModInitializer {
    private boolean rightShiftWasPressed = false;
    private boolean rKeyWasPressed = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getWindow() == null) return;
            long window = client.getWindow().getHandle();

            // Mở GUI ẩn bằng Right Shift
            boolean rightShiftIsDown = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
            if (rightShiftIsDown && !rightShiftWasPressed) {
                client.setScreen(new MyCustomCheatScreen(Text.literal("Bảng Điều Khiển")));
            }
            rightShiftWasPressed = rightShiftIsDown;

            // Dash 5-9 block bằng phím ẩn R
            boolean rKeyIsDown = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_R);
            if (rKeyIsDown && !rKeyWasPressed) {
                performDash(client.player);
            }
            rKeyWasPressed = rKeyIsDown;

            // Auto Sprint ngầm
            if (client.player.input.movementForward > 0 
                && !client.player.isSneaking() 
                && !client.player.isUsingItem()
                && !client.player.horizontalCollision) {
                client.player.setSprinting(true);
            }
        });
    }

    private static void performDash(ClientPlayerEntity player) {
        Vec3d lookDir = player.getRotationVector();
        Vec3d horizontalDir = new Vec3d(lookDir.x, 0, lookDir.z).normalize();
        double dashPower = 1.6;

        player.setVelocity(
            horizontalDir.x * dashPower,
            player.getVelocity().y + 0.1,
            horizontalDir.z * dashPower
        );

        player.getWorld().playSound(
            player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 
            SoundCategory.PLAYERS, 0.4f, 1.5f, false
        );
    }

    public static void toggleFlight(ClientPlayerEntity player) {
        if (player == null) return;
        boolean currentAllow = player.getAbilities().allowFlying;
        player.getAbilities().allowFlying = !currentAllow;
        if (!player.getAbilities().allowFlying) {
            player.getAbilities().flying = false;
        }
        player.sendAbilitiesUpdate();
        player.sendMessage(Text.literal("Trạng thái Bay: " + (player.getAbilities().allowFlying ? "BẬT" : "TẮT")), true);
    }
}
