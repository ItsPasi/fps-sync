package com.fpssync.mixin;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Window.class)
public class WindowMixin {

    @ModifyVariable(method = "setFramerateLimit", at = @At("HEAD"), argsOnly = true)
    private int fpssync$fixCustomFpsValues(int framerateLimit) {
        if (framerateLimit <= 0) {return Integer.MAX_VALUE;}
        if (framerateLimit >= 1010) {return Integer.MAX_VALUE;}
        return framerateLimit;
    }
}