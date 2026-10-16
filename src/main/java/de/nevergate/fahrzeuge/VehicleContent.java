package de.nevergate.fahrzeuge;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class VehicleContent {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NgFahrzeuge.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NgFahrzeuge.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NgFahrzeuge.MOD_ID);

    public static final RegistryObject<EntityType<NgCityEntity>> NG_CITY = registerVehicle(VehicleSpec.CITY);
    public static final RegistryObject<EntityType<NgCityEntity>> NG_FAMILY = registerVehicle(VehicleSpec.FAMILY);
    public static final RegistryObject<EntityType<NgCityEntity>> NG_TRANSPORTER = registerVehicle(VehicleSpec.TRANSPORTER);
    private static RegistryObject<EntityType<NgCityEntity>> registerVehicle(VehicleSpec spec) {
        return ENTITIES.register(spec.id,
                () -> EntityType.Builder.<NgCityEntity>of((type, level) -> new NgCityEntity(type, level, spec), MobCategory.MISC)
                    .sized(spec.width, spec.height)
                    .clientTrackingRange(10).updateInterval(3)
                    .build(NgFahrzeuge.MOD_ID + ":" + spec.id));
    }
    public static final RegistryObject<Item> NG_CITY_ITEM = ITEMS.register(
            "ng_city", () -> new VehiclePlacementItem(new Item.Properties().stacksTo(1), VehicleSpec.CITY));
    public static final RegistryObject<Item> NG_FAMILY_ITEM = ITEMS.register(
            "ng_family", () -> new VehiclePlacementItem(new Item.Properties().stacksTo(1), VehicleSpec.FAMILY));
    public static final RegistryObject<Item> NG_TRANSPORTER_ITEM = ITEMS.register(
            "ng_transporter", () -> new VehiclePlacementItem(new Item.Properties().stacksTo(1), VehicleSpec.TRANSPORTER));
    public static final RegistryObject<CreativeModeTab> VEHICLES = TABS.register(
            "vehicles", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ng_fahrzeuge.vehicles"))
                    .icon(() -> NG_CITY_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(NG_CITY_ITEM.get()); output.accept(NG_FAMILY_ITEM.get());
                        output.accept(NG_TRANSPORTER_ITEM.get());
                    })
                    .build());

    public static EntityType<NgCityEntity> type(VehicleSpec spec) {
        return switch (spec) {
            case CITY -> NG_CITY.get(); case FAMILY -> NG_FAMILY.get(); case TRANSPORTER -> NG_TRANSPORTER.get();
        };
    }

    public static Item item(VehicleSpec spec) {
        return switch (spec) {
            case CITY -> NG_CITY_ITEM.get(); case FAMILY -> NG_FAMILY_ITEM.get(); case TRANSPORTER -> NG_TRANSPORTER_ITEM.get();
        };
    }

    private VehicleContent() {}
}
