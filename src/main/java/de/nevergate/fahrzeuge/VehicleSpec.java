package de.nevergate.fahrzeuge;

import net.minecraft.world.item.DyeColor;

/** Geometry and seating of the three independently registered vehicle types. */
public enum VehicleSpec {
    CITY("ng_city", 1.8F, 2.6F, 1.4F, 1, DyeColor.CYAN),
    FAMILY("ng_family", 1.9F, 3.6F, 1.55F, 4, DyeColor.CYAN),
    TRANSPORTER("ng_transporter", 2.2F, 4.2F, 2.5F, 2, DyeColor.WHITE),
    TOW_TRUCK("ng_tow_truck", 2.4F, 6.0F, 2.5F, 2, DyeColor.YELLOW);

    public final String id;
    public final float width, length, height;
    public final int seats;
    public final DyeColor defaultColor;

    VehicleSpec(String id, float width, float length, float height, int seats, DyeColor color) {
        this.id = id; this.width = width; this.length = length; this.height = height;
        this.seats = seats; this.defaultColor = color;
    }
}
