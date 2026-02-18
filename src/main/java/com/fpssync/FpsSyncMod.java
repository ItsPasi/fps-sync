package com.fpssync;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class FpsSyncMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Apply saved fps value on startup
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            int value = client.options.getMaxFps().getValue();
            if (value == 0) {
                FrameLimiter.setEnabled(true);
                if (client.getInactivityFpsLimiter() != null) {
                    client.getInactivityFpsLimiter().setMaxFps(Integer.MAX_VALUE);
                }
            } else {
                FrameLimiter.setEnabled(false);
                if (client.getInactivityFpsLimiter() != null) {
                    client.getInactivityFpsLimiter().setMaxFps(value);
                }
            }
        });
    }
}