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
    public static final RegistryObject<net.minecraft.world.level.block.Block> DEALER=BLOCKS.register("vehicle_dealer",()->new VehicleServiceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(3.5F),0));
    public static final RegistryObject<net.minecraft.world.level.block.Block> GARAGE=BLOCKS.register("vehicle_garage",()->new VehicleServiceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(3.5F),1));
    public static final RegistryObject<net.minecraft.world.level.block.Block> WASH=BLOCKS.register("vehicle_wash",()->new VehicleWashBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(3)));
    public static final RegistryObject<net.minecraft.world.level.block.Block> LIFT=BLOCKS.register("vehicle_lift",()->new VehicleServiceBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(4F),2));
    public static final RegistryObject<net.minecraft.world.level.block.entity.BlockEntityType<VehicleLiftEntity>> LIFT_ENTITY=BLOCK_ENTITIES.register("vehicle_lift",()->net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(VehicleLiftEntity::new,LIFT.get()).build(null));
    public static final DeferredRegister<net.minecraft.world.inventory.MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, NgFahrzeuge.MOD_ID);
    public static final RegistryObject<net.minecraft.world.level.block.Block> WORKBENCH = BLOCKS.register("vehicle_workbench", () -> new VehicleWorkbenchBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().strength(3.5F).sound(net.minecraft.world.level.block.SoundType.METAL)));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<VehicleWorkbenchMenu>> WORKBENCH_MENU = MENUS.register("vehicle_workbench", () -> new net.minecraft.world.inventory.MenuType<>(VehicleWorkbenchMenu::new, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<VehicleManagementMenu>> MANAGEMENT_MENU = MENUS.register("management",()->net.minecraftforge.common.extensions.IForgeMenuType.create((id,inventory,data)->new VehicleManagementMenu(id,inventory,data.readVarInt())));
    public static final RegistryObject<net.minecraft.world.inventory.MenuType<VehicleServiceMenu>> SERVICE_MENU=MENUS.register("vehicle_service",()->net.minecraftforge.common.extensions.IForgeMenuType.create(VehicleServiceMenu::new));
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
    public static final RegistryObject<EntityType<NgCityEntity>> NG_TOW_TRUCK=registerVehicle(VehicleSpec.TOW_TRUCK);
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
    public static final RegistryObject<Item> DEALER_ITEM=ITEMS.register("vehicle_dealer",()->new net.minecraft.world.item.BlockItem(DEALER.get(),new Item.Properties()));
    public static final RegistryObject<Item> GARAGE_ITEM=ITEMS.register("vehicle_garage",()->new net.minecraft.world.item.BlockItem(GARAGE.get(),new Item.Properties()));
    public static final RegistryObject<Item> LIFT_ITEM=ITEMS.register("vehicle_lift",()->new net.minecraft.world.item.BlockItem(LIFT.get(),new Item.Properties()));
    public static final RegistryObject<Item> TOW_TRUCK_ITEM=ITEMS.register("ng_tow_truck",()->new VehiclePlacementItem(new Item.Properties().stacksTo(1),VehicleSpec.TOW_TRUCK));
    public static final RegistryObject<Item> FAMILY_ROOF=ITEMS.register("ng_family_roof",()->new VehiclePlacementItem(new Item.Properties().stacksTo(1),VehicleSpec.FAMILY,1));
    public static final RegistryObject<Item> VAN_WINDOWS=ITEMS.register("ng_transporter_windows",()->new VehiclePlacementItem(new Item.Properties().stacksTo(1),VehicleSpec.TRANSPORTER,1));
    public static final RegistryObject<Item> WASH_ITEM=ITEMS.register("vehicle_wash",()->new net.minecraft.world.item.BlockItem(WASH.get(),new Item.Properties()));
    public static final RegistryObject<Item> RACK=ITEMS.register("roof_rack",()->new VehicleRecoveryToolItem(new Item.Properties().stacksTo(1),"rack"));
    public static final RegistryObject<Item> JACK=ITEMS.register("vehicle_jack",()->new VehicleRecoveryToolItem(new Item.Properties().stacksTo(1),"jack"));
    public static final RegistryObject<Item> SPARE_WHEEL=ITEMS.register("spare_wheel",()->new VehicleRecoveryToolItem(new Item.Properties().stacksTo(1),"wheel"));
    public static final RegistryObject<Item> BOX_TRAILER=ITEMS.register("box_trailer",()->new TrailerPlacementItem(new Item.Properties().stacksTo(1),1));
    public static final RegistryObject<Item> CANVAS_TRAILER=ITEMS.register("canvas_trailer",()->new TrailerPlacementItem(new Item.Properties().stacksTo(1),2));
    public static final RegistryObject<Item> ROPE=ITEMS.register("tow_rope",()->new VehicleRecoveryToolItem(new Item.Properties().stacksTo(1),"rope"));
    public static final RegistryObject<Item> JUMPER=ITEMS.register("jumper_cables",()->new VehicleRecoveryToolItem(new Item.Properties().stacksTo(1),"jumper"));
    public static final RegistryObject<Item> REMOTE=ITEMS.register("recovery_remote",()->new VehicleRecoveryToolItem(new Item.Properties().stacksTo(1),"remote"));
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
                        output.accept(PARTS.get());output.accept(PAPERS.get());output.accept(PUMP_ITEM.get());output.accept(TRAILER_ITEM.get());output.accept(TOW_TRUCK_ITEM.get());output.accept(FAMILY_ROOF.get());output.accept(VAN_WINDOWS.get());output.accept(ROPE.get());output.accept(JUMPER.get());output.accept(REMOTE.get());output.accept(DEALER_ITEM.get());output.accept(GARAGE_ITEM.get());output.accept(LIFT_ITEM.get());output.accept(WASH_ITEM.get());output.accept(RACK.get());output.accept(JACK.get());output.accept(SPARE_WHEEL.get());output.accept(BOX_TRAILER.get());output.accept(CANVAS_TRAILER.get());
                    })
                    .build());

    public static EntityType<NgCityEntity> type(VehicleSpec spec) {
        return switch (spec) {
            case CITY -> NG_CITY.get(); case FAMILY -> NG_FAMILY.get(); case TRANSPORTER -> NG_TRANSPORTER.get(); case TOW_TRUCK -> NG_TOW_TRUCK.get();
        };
    }

    public static Item item(VehicleSpec spec) {
        return switch (spec) {
            case CITY -> NG_CITY_ITEM.get(); case FAMILY -> NG_FAMILY_ITEM.get(); case TRANSPORTER -> NG_TRANSPORTER_ITEM.get(); case TOW_TRUCK -> TOW_TRUCK_ITEM.get();
        };
    }

    private VehicleContent() {}
}
