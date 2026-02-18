package com.fpssync.mixin;

import com.fpssync.FrameLimiter;
import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow @Final @Mutable
    private SimpleOption<Integer> maxFps;

    // Replaces the vanilla FPS slider with a stepped 0-260 range, where 0 = FPS Sync
    @Inject(method = "<init>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/option/GameOptions;load()V",
            shift = At.Shift.BEFORE))
    private void overrideFpsSlider(CallbackInfo ci) {
        this.maxFps = new SimpleOption<>(
                "options.framerateLimit",
                SimpleOption.emptyTooltip(),
                (optionText, value) -> {
                    if (value == 0) return Text.literal("FPS-Sync");
                    if (value == 260) return Text.translatable("options.framerateLimit.max");
                    return Text.translatable("options.framerate", value);
                },
                new SimpleOption.ValidatingIntSliderCallbacks(0, 26).withModifier(
                        (IntFunction<Integer>) sliderPos -> sliderPos * 10,
                        (ToIntFunction<Integer>) value -> Math.min(value / 10, 26),
                        true
                ),
                Codec.intRange(0, 260),
                120,
                value -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    FrameLimiter.setEnabled(value == 0);
                    if (client != null && client.getInactivityFpsLimiter() != null) {
                        client.getInactivityFpsLimiter().setMaxFps(
                                value == 0 ? Integer.MAX_VALUE : value
                        );
                    }
                }
        );
    }
}