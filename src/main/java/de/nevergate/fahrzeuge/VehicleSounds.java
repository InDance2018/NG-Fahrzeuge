package de.nevergate.fahrzeuge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class VehicleSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, NgFahrzeuge.MOD_ID);
    public static final RegistryObject<SoundEvent> START = sound("engine_start"), CITY = sound("engine_city"),
            FAMILY = sound("engine_family"), VAN = sound("engine_transporter"), ROAD = sound("road"),
            HORN = sound("horn"), INDICATOR = sound("indicator"), LOCK = sound("lock"), REVERSE=sound("reverse_warning");
    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(NgFahrzeuge.MOD_ID, name)));
    }
    public static SoundEvent engine(VehicleSpec spec) {
        return switch(spec) { case CITY -> CITY.get(); case FAMILY -> FAMILY.get(); case TRANSPORTER,TOW_TRUCK -> VAN.get(); };
    }
    private VehicleSounds() {}
}
