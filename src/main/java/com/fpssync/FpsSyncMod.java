package com.fpssync;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class FpsSyncMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            int value = client.options.getMaxFps().getValue();
            if (value <= 0) {
                FrameLimiter.setEnabled(true);
                FrameLimiter.setManualLimit(0);
                if (client.getWindow() != null) {
                    client.getWindow().setFramerateLimit(Integer.MAX_VALUE);
                }
                return;
            }
            FrameLimiter.setEnabled(false);
            FrameLimiter.setManualLimit(value >= 1010 ? 0 : value);
            if (client.getWindow() != null) {
                client.getWindow().setFramerateLimit(value >= 1010 ? Integer.MAX_VALUE : value);
            }
        });
    }
}