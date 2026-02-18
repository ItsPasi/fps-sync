package com.fpssync;

public class FrameLimiter {

    private static long lastFrameTime = 0;
    private static boolean enabled = false;

    public static void setEnabled(boolean value) {
        enabled = value;
        lastFrameTime = 0;
    }

    public static void limitFrame() {
        if (!enabled) return;

        MonitorInfoProvider.updateDisplayInfo();

        int targetFps = MonitorInfoProvider.getRefreshRate();
        if (targetFps <= 0) return;

        long frameBudgetNs = 1_000_000_000L / targetFps;
        long now = System.nanoTime();

        if (lastFrameTime == 0) { lastFrameTime = now; return; }

        long nextFrameTime = lastFrameTime + frameBudgetNs;

        if (now >= nextFrameTime) { lastFrameTime = now; return; }

        // Sleep for most of the budget, spin-wait the final 1ms for precision
        try {
            long sleepMs = (nextFrameTime - now) / 1_000_000L;
            if (sleepMs > 1) Thread.sleep(sleepMs - 1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        while (System.nanoTime() < nextFrameTime) {
            Thread.onSpinWait();
        }

        lastFrameTime = nextFrameTime;
    }
}