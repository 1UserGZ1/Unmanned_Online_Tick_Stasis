package com.dyxiaojiazi.unmannedonlinetickstasis.mixin;

import com.dyxiaojiazi.unmannedonlinetickstasis.UnmannedOnlineTickStasis;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (UnmannedOnlineTickStasis.isPaused()) {
            // 跳过整个世界 tick
            ci.cancel();
        }
    }
}