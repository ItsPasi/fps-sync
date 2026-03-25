package com.fpssync;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

public class MonitorInfoProvider {

    private static long lastMonitorHandle = 0;
    private static int lastRefreshRate = 60;
    private static long lastCheckTime = 0;
    private static final long CHECK_INTERVAL_NS = 1_000_000_000L;

    // Checks for monitor changes
    public static void updateDisplayInfo() {
        long now = System.nanoTime();
        if (now - lastCheckTime < CHECK_INTERVAL_NS) return;
        lastCheckTime = now;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) return;

        long window = client.getWindow().getHandle();
        long monitor = GLFW.glfwGetWindowMonitor(window);

        // In windowed mode glfwGetWindowMonitor returns 0; detect from window position instead
        if (monitor == 0) {
            monitor = getMonitorFromWindowPosition(window,
                    client.getWindow().getWidth(), client.getWindow().getHeight());
        }

        if (monitor != lastMonitorHandle) {
            lastRefreshRate = detectRefreshRate(monitor);
            lastMonitorHandle = monitor;
        }
    }

    public static int getRefreshRate() {
        return lastRefreshRate;
    }

    // Finds which monitor contains the center of the window
    private static long getMonitorFromWindowPosition(long window, int width, int height) {
        int[] x = new int[1], y = new int[1];
        GLFW.glfwGetWindowPos(window, x, y);
        int cx = x[0] + width / 2, cy = y[0] + height / 2;

        long result = GLFW.glfwGetPrimaryMonitor();
        PointerBuffer monitors = GLFW.glfwGetMonitors();
        if (monitors != null) {
            for (int i = 0; i < monitors.limit(); i++) {
                long m = monitors.get(i);
                int[] mx = new int[1], my = new int[1];
                GLFW.glfwGetMonitorPos(m, mx, my);
                GLFWVidMode mode = GLFW.glfwGetVideoMode(m);
                if (mode == null) continue;
                if (cx >= mx[0] && cx < mx[0] + mode.width() &&
                        cy >= my[0] && cy < my[0] + mode.height()) {
                    result = m;
                    break;
                }
            }
        }
        return result;
    }

    private static int detectRefreshRate(long monitor) {
        GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
        return mode != null ? mode.refreshRate() : 60;
    }
}