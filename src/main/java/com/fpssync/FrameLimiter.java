package com.fpssync;

public class FrameLimiter {

    private static long lastFrameTime = 0;
    private static boolean fpsSyncEnabled = false;
    private static int manualFpsLimit = 0;

    public static void setEnabled(boolean value) {
        fpsSyncEnabled = value;
        lastFrameTime = 0;
    }
    public static void setManualLimit(int fps) {
        manualFpsLimit = fps;
        lastFrameTime = 0;
    }

    public static void limitFrame() {
        int targetFps;

        if (fpsSyncEnabled) {
            MonitorInfoProvider.updateDisplayInfo();
            targetFps = MonitorInfoProvider.getRefreshRate();
        } else if (manualFpsLimit > 0 && manualFpsLimit < 1010) {
            targetFps = manualFpsLimit;
        } else {
            return; // Unlimited (manualFpsLimit == 0 or >= 1010)
        }

        if (targetFps <= 0) return;

        long frameBudgetNs = 1_000_000_000L / targetFps;
        long now = System.nanoTime();

        if (lastFrameTime == 0) { lastFrameTime = now; return; }

        long nextFrameTime = lastFrameTime + frameBudgetNs;

        if (now >= nextFrameTime) { lastFrameTime = now; return; }

        try {
            long sleepMs = (nextFrameTime - now) / 1_000_000L - 1;
            if (sleepMs > 0) Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        while (System.nanoTime() < nextFrameTime) {
            Thread.onSpinWait();
        }

        lastFrameTime = nextFrameTime;
    }
}