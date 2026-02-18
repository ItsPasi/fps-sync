package com.fpssync.mixin;

import net.caffeinemc.mods.sodium.api.config.option.ControlValueFormatter;
import net.caffeinemc.mods.sodium.api.config.structure.IntegerOptionBuilder;
import net.caffeinemc.mods.sodium.client.gui.SodiumConfigBuilder;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SodiumConfigBuilder.class, remap = false)
public abstract class SodiumConfigBuilderMixin {

    // Extends Sodium's FPS slider range to include 0 (FPS Sync)
    @Redirect(method = "buildGeneralPage", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;setRange(III)Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;",
                    ordinal = 3))
    public IntegerOptionBuilder redirectFpsSliderRange(
            IntegerOptionBuilder builder, int min, int max, int step) {
        return builder.setRange(0, max, 10);
    }

    // Replaces Sodium's FPS formatter — detected at runtime by testing for numeric output
    @Redirect(method = "buildGeneralPage", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;setValueFormatter(Lnet/caffeinemc/mods/sodium/api/config/option/ControlValueFormatter;)Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;"))
    public IntegerOptionBuilder redirectAnyValueFormatter(
            IntegerOptionBuilder builder, ControlValueFormatter original) {
        try {
            if (original.format(120).getString().contains("120")) {
                return builder.setValueFormatter(value -> {
                    if (value == 0) return Text.literal("FPS-Sync");
                    if (value == 260) return Text.translatable("options.framerateLimit.max");
                    return Text.translatable("options.framerate", value);
                });
            }
        } catch (Exception ignored) {}
        return builder.setValueFormatter(original);
    }
}