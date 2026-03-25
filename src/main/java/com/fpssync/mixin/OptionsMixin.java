package com.fpssync.mixin;

import com.fpssync.FrameLimiter;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {

    @Shadow @Final @Mutable
    private OptionInstance<Integer> framerateLimit;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void overrideFpsSlider(CallbackInfo ci) {
        this.framerateLimit = new OptionInstance<>(
                "options.framerateLimit",
                OptionInstance.noTooltip(),
                (optionText, value) -> {
                    if (value == -10) return Component.literal("FPS Sync");
                    if (value > 1000) return Component.translatable("options.framerateLimit.max");
                    return Component.translatable("options.framerate", value);
                },
                new OptionInstance.IntRange(0, 101).xmap(
                        sliderPos -> {
                            if (sliderPos == 0) return -10;
                            if (sliderPos == 101) return 1010;
                            return sliderPos * 10;
                        },
                        value -> {
                            if (value < 0) return 0;
                            if (value > 1000) return 101;
                            return Math.min(value / 10, 100);
                        },
                        true
                ),
                Codec.intRange(-10, 1010),
                120,
                value -> {
                    Minecraft client = Minecraft.getInstance();
                    if (value == -10) {
                        FrameLimiter.setEnabled(true);
                        client.getFramerateLimitTracker().setFramerateLimit(Integer.MAX_VALUE);
                    } else {
                        FrameLimiter.setEnabled(false);
                        FrameLimiter.setManualLimit(value >= 1010 ? 0 : value);
                        client.getFramerateLimitTracker().setFramerateLimit(value >= 1010 ? Integer.MAX_VALUE : value);
                    }
                }
        );
        ((Options)(Object)this).load();
    }
}