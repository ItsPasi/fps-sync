package com.fpssync;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class FpsSyncMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            int value = client.options.framerateLimit().get();
            if (value == -10) {
                FrameLimiter.setEnabled(true);
                client.getFramerateLimitTracker().setFramerateLimit(Integer.MAX_VALUE);
            } else {
                FrameLimiter.setEnabled(false);
                FrameLimiter.setManualLimit(value >= 1010 ? 0 : value);
                // Set InactivityFpsLimiter so debug overlay shows correct value
                client.getFramerateLimitTracker().setFramerateLimit(value >= 1010 ? Integer.MAX_VALUE : value);
            }
        });
    }
}