package com.tenban.autocart.mixin;

import com.tenban.autocart.AutoCartMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    
    // Tiêm code vào hàm tryUseTotem của Minecraft
    @Inject(method = "tryUseTotem", at = @At("RETURN"))
    private void onTotemPop(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        // Nếu trả về true (nghĩa là Totem đã kích hoạt cứu mạng thành công)
        if (cir.getReturnValue()) {
            LivingEntity entity = (LivingEntity) (Object) this;
            
            // Kiểm tra xem người vừa nổ totem có phải là người chơi không
            if (entity instanceof ServerPlayerEntity player) {
                
                // --- ĐẶT DÒNG CODE ĐÓ NGAY TẠI ĐÂY ---
                AutoCartMod.tasks.add(new AutoCartMod.CartComboTask(player));
                
            }
        }
    }
}
