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
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NgFahrzeuge.MOD_ID);
    public static final DeferredRegister<net.minecraft.world.level.block.entity.BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NgFahrzeuge.MOD_ID);
    public static final RegistryObject<net.minecraft.world.level.block.Block> PUMP = BLOCKS.register("fuel_pump",()->new FuelPumpBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(3.5F).sound(net.minecraft.world.level.block.SoundType.METAL)));
    public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<FuelPumpEntity>> PUMP_ENTITY = BLOCK_ENTITIES.register("fuel_pump",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(FuelPumpEntity::new,PUMP.get()).build(null));
    public static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, NgFahrzeuge.MOD_ID);
    public static final RegistryObject<net.minecraft.world.level.block.Block> WORKBENCH = BLOCKS.register("vehicle_workbench", () -> new VehicleWorkbenchBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(3.5F).sound(net.minecraft.world.level.block.SoundType.METAL)));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<VehicleWorkbenchMenu>> WORKBENCH_MENU = MENUS.register("vehicle_workbench", () -> new net.minecraft.world.inventory.MenuType<>(VehicleWorkbenchMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<VehicleManagementMenu>> MANAGEMENT_MENU = MENUS.register("management",()->net.minecraftforge.common.extensions.IForgeMenuType.create((id,inventory,data)->new VehicleManagementMenu(id,inventory,data.readVarInt())));
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NgFahrzeuge.MOD_ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NgFahrzeuge.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NgFahrzeuge.MOD_ID);

    public static final RegistryObject<EntityType<TrailerEntity>> TRAILER = ENTITIES.register("cargo_trailer",()->EntityType.Builder.<TrailerEntity>of(TrailerEntity::new,MobCategory.MISC).sized(1.6F,1.2F).clientTrackingRange(10).updateInterval(3).build("ng_fahrzeuge:cargo_trailer"));
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
    public static final RegistryObject<Item> FUEL_CAN=ITEMS.register("fuel_can",()->new FuelCanItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> REPAIR_TOOL=ITEMS.register("repair_tool",()->new Item(new Item.Properties().durability(128)));
    public static final RegistryObject<Item> PARTS=ITEMS.register("spare_parts",()->new Item(new Item.Properties()));
    public static final RegistryObject<Item> PAPERS=ITEMS.register("vehicle_papers",()->new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PUMP_ITEM=ITEMS.register("fuel_pump",()->new net.minecraft.world.item.BlockItem(PUMP.get(),new Item.Properties()));
    public static final RegistryObject<Item> TRAILER_ITEM=ITEMS.register("cargo_trailer",()->new TrailerPlacementItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> KEY = ITEMS.register("vehicle_key", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WORKBENCH_ITEM = ITEMS.register("vehicle_workbench", () -> new net.minecraft.world.item.BlockItem(WORKBENCH.get(), new Item.Properties()));
    public static final RegistryObject<CreativeModeTab> VEHICLES = TABS.register(
            "vehicles", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ng_fahrzeuge.vehicles"))
                    .icon(() -> NG_CITY_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(NG_CITY_ITEM.get()); output.accept(NG_FAMILY_ITEM.get());
                        output.accept(NG_TRANSPORTER_ITEM.get()); output.accept(KEY.get()); output.accept(WORKBENCH_ITEM.get());output.accept(FUEL_CAN.get());output.accept(REPAIR_TOOL.get());
                        output.accept(PARTS.get());output.accept(PAPERS.get());output.accept(PUMP_ITEM.get());output.accept(TRAILER_ITEM.get());
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
