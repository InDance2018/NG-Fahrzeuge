package de.nevergate.fahrzeuge.client;

/** Pixel-based sizing: Minecraft's menu scale must not enlarge the driving HUD. */
public record HudLayout(double scale, double x, double y, int width, int height) {
    public static HudLayout fit(int guiWidth, int guiHeight, double guiScale,
                                double preference, boolean details) {
        int width = 200, height = details ? 228 : 74;
        double pixelsWide = guiWidth * guiScale, pixelsHigh = guiHeight * guiScale;
        double margin = 12.0 / guiScale;
        double bottom = Math.max(32.0, 56.0 / guiScale);
        double desired = Double.isFinite(preference) ? Math.max(0.6, Math.min(1.4, preference)) : 1.0;
        double pixelScale = Math.min(2.0 * desired, Math.min(pixelsWide * 0.28 / width,
                Math.min(pixelsHigh * (details ? 0.46 : 0.16) / height,
                        Math.max(1, guiHeight - bottom - margin) * guiScale / height)));
        double scale = pixelScale / guiScale;
        return new HudLayout(scale, (guiWidth - margin) / scale - width,
                Math.max(margin / scale, (guiHeight - bottom) / scale - height), width, height);
    }
}
