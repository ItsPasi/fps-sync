package com.fpssync.mixin;

import net.caffeinemc.mods.sodium.client.gui.options.Option;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatter;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SliderControl.class, remap = false)
public abstract class SodiumConfigBuilderMixin {

    @Mutable @Shadow @Final private int min;
    @Mutable @Shadow @Final private int max;
    @Mutable @Shadow @Final private int interval;
    @Mutable @Shadow @Final private ControlValueFormatter mode;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void fpssync$modifyFpsSlider(Option<Integer> option, int min, int max, int interval, ControlValueFormatter mode, CallbackInfo ci) {
        if (!fpssync$isFramerateLimitOption(option, min, max, interval)) {return;}

        this.min = -10;
        this.max = 1010;
        this.interval = 10;
        this.mode = value -> {
            if (value <= 0) {return Text.literal("FPS Sync");}
            if (value >= 1010) {return Text.translatable("options.framerateLimit.max");}
            return Text.translatable("options.framerate", value);
        };
    }

    @Unique
    private static boolean fpssync$isFramerateLimitOption(Option<Integer> option, int min, int max, int interval) {
        if (min != 10 || max != 260 || interval != 10) {return false;}
        if (option.getName().getContent() instanceof TranslatableTextContent content) {return "options.framerateLimit".equals(content.getKey());}
        return false;
    }
}