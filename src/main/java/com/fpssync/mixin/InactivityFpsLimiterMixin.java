package com.fpssync.mixin;

import net.minecraft.client.option.InactivityFpsLimiter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(InactivityFpsLimiter.class)
public class InactivityFpsLimiterMixin {

    @ModifyVariable(method = "setMaxFps", at = @At("HEAD"), argsOnly = true)
    private int fpssync$fixCustomFpsValues(int maxFps) {
        if (maxFps <= 0) {return Integer.MAX_VALUE;}
        if (maxFps >= 1010) {return Integer.MAX_VALUE;}
        return maxFps;
    }
}