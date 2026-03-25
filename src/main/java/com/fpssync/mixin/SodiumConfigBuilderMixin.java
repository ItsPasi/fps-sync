package com.fpssync.mixin;

import net.caffeinemc.mods.sodium.api.config.option.ControlValueFormatter;
import net.caffeinemc.mods.sodium.api.config.structure.IntegerOptionBuilder;
import net.caffeinemc.mods.sodium.client.gui.SodiumConfigBuilder;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SodiumConfigBuilder.class, remap = false)
public abstract class SodiumConfigBuilderMixin {

    @Redirect(method = "buildGeneralPage", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;setRange(III)Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;",
                    ordinal = 3))
    public IntegerOptionBuilder redirectFpsSliderRange(
            IntegerOptionBuilder builder, int min, int max, int step) {
        return builder.setRange(-10, 1010, 10);
    }

    @Redirect(method = "buildGeneralPage", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;setValueFormatter(Lnet/caffeinemc/mods/sodium/api/config/option/ControlValueFormatter;)Lnet/caffeinemc/mods/sodium/api/config/structure/IntegerOptionBuilder;",
                    ordinal = 5))
    public IntegerOptionBuilder redirectFpsValueFormatter(
            IntegerOptionBuilder builder, ControlValueFormatter original) {
        return builder.setValueFormatter(value -> {
            if (value == -10) return Component.literal("FPS Sync");
            if (value == 0) return Component.literal("FPS Sync");
            if (value >= 1010) return Component.translatable("options.framerateLimit.max");
            return Component.translatable("options.framerate", value);
        });
    }
}