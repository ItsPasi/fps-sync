package com.fpssync.mixin;

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "me.jellysquid.mods.sodium.client.gui.options.control.SliderControl$Button", remap = false)
public abstract class SodiumSliderButtonMixin {

    @Redirect(
            method = "renderSlider",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;valueOf(I)Ljava/lang/String;"
            )
    )
    private String fpssync$formatHoveredSliderValue(int value) {
        if (value <= 0) {return "FPS Sync";}
        if (value >= 1010) {return Text.translatable("options.framerateLimit.max").getString();}
        return String.valueOf(value);
    }
}