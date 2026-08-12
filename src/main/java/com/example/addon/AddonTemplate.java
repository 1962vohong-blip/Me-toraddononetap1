package com.tenban.autocart;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AutoCartMod implements ModInitializer {
    // Danh sách lưu các tiến trình combo đang chạy
    public static final List<CartComboTask> tasks = new ArrayList<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<CartComboTask> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                CartComboTask task = iterator.next();
                task.ticksElapsed++; // Tăng biến đếm sau mỗi tick

                ServerPlayerEntity player = task.player;
                ServerWorld world = player.getServerWorld();

                // Tick 3: Đặt đường ray (Rail)
                if (task.ticksElapsed == 3) {
                    if (player.isAlive() && !player.isDisconnected()) {
                        task.targetPos = player.getBlockPos();
                        world.setBlockState(task.targetPos, Blocks.RAIL.getDefaultState());
                    } else {
                        iterator.remove(); // Hủy nếu người chơi ngỏm hoặc thoát
                    }
                }
                // Tick 6: Đặt xe mỏ TNT ngay trên đường ray vừa tạo
                else if (task.ticksElapsed == 6) {
                    if (task.targetPos != null) {
                        task.tntCart = new TntMinecartEntity(EntityType.TNT_MINECART, world);
                        // Căn giữa xe mỏ vào tâm đường ray để chuẩn xác vật lý
                        task.tntCart.setPosition(task.targetPos.getX() + 0.5, task.targetPos.getY() + 0.5, task.targetPos.getZ() + 0.5);
                        world.spawnEntity(task.tntCart);
                    }
                }
                // Tick 9: Bắn nổ bằng lửa (mô phỏng nỏ lửa/cung lửa)
                else if (task.ticksElapsed >= 9) {
                    if (task.tntCart != null && task.tntCart.isAlive()) {
                        // Kích hoạt nổ thông qua nguồn sát thương lửa (giống như trúng mũi tên lửa)
                        task.tntCart.damage(world, world.getDamageSources().onFire(), 1.0f);
                    }
                    iterator.remove(); // Hoàn thành chuỗi combo, xóa khỏi danh sách
                }
            }
        });
    }

    // Lớp lưu trữ trạng thái của từng nạn nhân đang dính bẫy
    public static class CartComboTask {
        public ServerPlayerEntity player;
        public int ticksElapsed;
        public BlockPos targetPos;
        public TntMinecartEntity tntCart;

        public CartComboTask(ServerPlayerEntity player) {
            this.player = player;
            this.ticksElapsed = 0; // Bắt đầu tính từ lúc nổ Totem (tick 0)
        }
    }
}
