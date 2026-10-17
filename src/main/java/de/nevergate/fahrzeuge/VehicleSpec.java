package de.nevergate.fahrzeuge;

import net.minecraft.world.item.DyeColor;

/** Geometry and seating of the three independently registered vehicle types. */
public enum VehicleSpec {
    CITY("ng_city", 1.8F, 2.6F, 1.4F, 1, DyeColor.CYAN),
    FAMILY("ng_family", 1.9F, 3.6F, 1.55F, 4, DyeColor.CYAN),
    TRANSPORTER("ng_transporter", 2.2F, 4.2F, 2.5F, 2, DyeColor.WHITE),
    TOW_TRUCK("ng_tow_truck", 2.4F, 6.0F, 2.5F, 2, DyeColor.YELLOW),
    BUS("ng_bus", 2.5F, 8F, 3F, 13, DyeColor.RED),
    MOTORCYCLE("ng_motorcycle", .85F, 2.2F, 1.3F, 2, DyeColor.BLACK),
    PICKUP("ng_pickup", 2.1F, 4.6F, 1.8F, 2, DyeColor.BLUE),
    OFFROAD("ng_offroad",2.15F,4.3F,2.05F,4,DyeColor.GREEN),
    SPORT("ng_sport",2F,4.1F,1.25F,2,DyeColor.RED),
    TAXI("ng_taxi",1.95F,4.2F,1.65F,4,DyeColor.YELLOW),
    TRACTOR("ng_tractor",2.5F,5.6F,3.2F,2,DyeColor.BLUE);

    public final String id;
    public final float width, length, height;
    public final int seats;
    public final DyeColor defaultColor;

    VehicleSpec(String id, float width, float length, float height, int seats, DyeColor color) {
        this.id = id; this.width = width; this.length = length; this.height = height;
        this.seats = seats; this.defaultColor = color;
    }
}
