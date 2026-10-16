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

    public static final RegistryObject<EntityType<NgCityEntity>> NG_CITY = ENTITIES.register(
            "ng_city", () -> EntityType.Builder.<NgCityEntity>of(NgCityEntity::new, MobCategory.MISC)
                    .sized(NgCityEntity.WIDTH, NgCityEntity.HEIGHT)
                    .clientTrackingRange(10).updateInterval(3)
                    .build(NgFahrzeuge.MOD_ID + ":ng_city"));
    public static final RegistryObject<Item> NG_CITY_ITEM = ITEMS.register(
            "ng_city", () -> new VehiclePlacementItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<CreativeModeTab> VEHICLES = TABS.register(
            "vehicles", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ng_fahrzeuge.vehicles"))
                    .icon(() -> NG_CITY_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> output.accept(NG_CITY_ITEM.get()))
                    .build());

    private VehicleContent() {}
}
