package de.nevergate.fahrzeuge.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraftforge.fml.loading.FMLPaths;

/** Local display preferences only; never changes vehicle or server data. */
public final class VehicleHudSettings {
    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("ng_fahrzeuge-hud.properties");
    private static double scale = load();
    private static boolean details;

    public static double scale() { return scale; }
    public static boolean details() { return details; }
    public static void toggleDetails() { details = !details; }
    public static void compact() { details = false; }

    public static void resize(int direction) {
        scale = Math.max(0.6, Math.min(1.4, Math.round((scale + direction * 0.1) * 10) / 10.0));
        Properties p = new Properties();
        p.setProperty("scale", Double.toString(scale));
        try {
            Files.createDirectories(FILE.getParent());
            try (var out = Files.newOutputStream(FILE)) { p.store(out, "NG-Fahrzeuge HUD scale (0.6 to 1.4)"); }
        } catch (IOException e) { LOGGER.warn("Could not save vehicle HUD size", e); }
    }

    private static double load() {
        if (!Files.isRegularFile(FILE)) return 1.0;
        Properties p = new Properties();
        try (var in = Files.newInputStream(FILE)) {
            p.load(in);
            double value = Double.parseDouble(p.getProperty("scale", "1.0"));
            return Double.isFinite(value) ? Math.max(0.6, Math.min(1.4, value)) : 1.0;
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.warn("Invalid vehicle HUD settings; using default size");
            return 1.0;
        }
    }

    private VehicleHudSettings() {}
}
