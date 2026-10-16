package de.nevergate.fahrzeuge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(value = NgFahrzeuge.MOD_ID, namespace = NgFahrzeuge.MOD_ID)
public final class EquipmentGameTests {
    private static NgCityEntity car(GameTestHelper h, VehicleSpec spec) {
        NgCityEntity car = VehicleContent.type(spec).create(h.getLevel());
        Vec3 p = h.absoluteVec(new Vec3(1.5, 0, 1.5));
        car.setPos(p.x, h.getLevel().getMaxBuildHeight() - 8, p.z);
        car.setNoGravity(true); h.getLevel().addFreshEntity(car);
        return car;
    }
    private static Player player(GameTestHelper h) {
        Player p = h.makeMockPlayer(); p.getAbilities().mayBuild = true;
        p.getAbilities().instabuild = false; return p;
    }
    private static Player enter(GameTestHelper h, NgCityEntity car) {
        Player p = player(h);
        h.assertTrue(car.interact(p, InteractionHand.MAIN_HAND).consumesAction(), "Entry rejected");
        return p;
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void allThreeTypesHaveCorrectDimensionsItemsAndVehicleTags(GameTestHelper h) {
        var tag = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("ng_verkehr", "parking_vehicles"));
        for (VehicleSpec spec : VehicleSpec.values()) {
            NgCityEntity car = car(h, spec);
            h.assertTrue(car.spec() == spec, "Factory returned wrong vehicle spec");
            h.assertTrue(Math.abs(car.getBbHeight()-spec.height) < 0.0001, "Entity height differs from spec");
            h.assertTrue(Math.abs(car.getBoundingBox().getZsize()-spec.length) < 0.0001, "Footprint differs from spec");
            h.assertTrue(car.getPickResult().is(VehicleContent.item(spec)), "Pickup returned wrong vehicle group");
            h.assertTrue(car.getType().is(tag), "Unoccupied vehicle missing optional parking tag");
            car.discard();
        }
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void familySeatsFourWithDistinctPositionsAndRejectsFifth(GameTestHelper h) {
        NgCityEntity car = car(h, VehicleSpec.FAMILY);
        Player[] people = new Player[4];
        for (int i=0;i<4;i++) people[i]=enter(h,car);
        h.assertTrue(car.getPassengers().size()==4 && car.getDriver()==people[0], "Family seat allocation failed");
        var positions = car.getPassengers().stream().map(car::getPassengerRidingPosition).distinct().count();
        h.assertTrue(positions==4, "Two passengers share a seat");
        Player fifth=player(h);
        h.assertFalse(car.interact(fifth,InteractionHand.MAIN_HAND).consumesAction(), "Fifth passenger entered");
        h.assertFalse(car.acceptInput(people[1],VehicleNetwork.FORWARD), "Passenger controlled vehicle");
        h.assertFalse(car.applyAction(people[2],VehicleNetwork.PARK_ACTION), "Passenger changed parking brake");
        people[0].stopRiding();
        h.assertTrue(car.getDriver()==null && car.isParkingBraked(), "Passenger inherited vacant driver seat");
        h.assertTrue(car.getPassengers().size()==3, "Driver exit removed other passengers");
        Player replacement=enter(h,car);
        h.assertTrue(car.getDriver()==replacement, "New entrant did not receive vacant driver seat");
        car.discard(); h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void transporterSeatsTwoAndKeepsTallerDimensionsAfterNbt(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.TRANSPORTER); enter(h,car); enter(h,car);
        h.assertFalse(car.interact(player(h),InteractionHand.MAIN_HAND).consumesAction(), "Third van passenger entered");
        CompoundTag saved=new CompoundTag();car.saveWithoutId(saved);
        NgCityEntity restored=VehicleContent.NG_TRANSPORTER.get().create(h.getLevel());restored.load(saved);
        h.assertTrue(restored.getBbHeight()==2.5F && restored.spec().seats==2, "Transporter loaded as a smaller group");
        car.discard(); h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void parkingBrakeOverridesThrottleAndCanBeReleasedAndReapplied(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.CITY);Player driver=enter(h,car);
        Vec3 start=car.position();car.acceptInput(driver,VehicleNetwork.FORWARD);
        for(int i=0;i<8;i++)car.tick();
        h.assertTrue(car.position().equals(start), "Parking brake did not hold vehicle");
        h.assertTrue(VehicleNetwork.applyAction(driver,new VehicleNetwork.Action(car.getId(),VehicleNetwork.PARK_ACTION)), "Driver could not release parking brake");
        for(int i=0;i<8;i++)car.tick();
        h.assertTrue(car.getDriveSpeed()>0, "Released vehicle did not move");
        car.applyAction(driver,VehicleNetwork.PARK_ACTION);
        for(int i=0;i<8;i++)car.tick();
        h.assertTrue(car.getDriveSpeed()==0, "Reapplied parking brake did not stop vehicle");
        car.discard();h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void lightsAndActionsRequireTheDriverAndRejectUnknownActions(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.FAMILY);Player driver=enter(h,car),rider=enter(h,car);
        h.assertFalse(VehicleNetwork.applyAction(rider,new VehicleNetwork.Action(car.getId(),VehicleNetwork.LIGHT_ACTION)), "Passenger changed lights");
        h.assertFalse(VehicleNetwork.applyAction(driver,new VehicleNetwork.Action(car.getId()+1,VehicleNetwork.LIGHT_ACTION)), "Wrong vehicle ID accepted for lights");
        h.assertFalse(VehicleNetwork.applyAction(driver,new VehicleNetwork.Action(car.getId(),255)), "Unknown action accepted");
        h.assertTrue(VehicleNetwork.applyAction(driver,new VehicleNetwork.Action(car.getId(),VehicleNetwork.LIGHT_ACTION))&&car.lightsOn(), "Headlights did not switch on");
        car.applyAction(driver,VehicleNetwork.LIGHT_ACTION);h.assertFalse(car.lightsOn(), "Headlights did not switch off");
        car.discard();h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void dyeConsumesOnceAndColorLightsAndBrakeSurviveNbt(GameTestHelper h) {
        for(VehicleSpec spec:VehicleSpec.values()) {
            NgCityEntity car=car(h,spec);Player p=player(h);
            p.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.RED_DYE,2));
            car.interact(p,InteractionHand.MAIN_HAND);car.interact(p,InteractionHand.MAIN_HAND);
            h.assertTrue(car.color()==DyeColor.RED.getId()&&p.getMainHandItem().getCount()==1, "Dye was not applied exactly once");
            CompoundTag equipment=new CompoundTag();car.saveEquipment(equipment);
            equipment.putBoolean("VehicleLights",true);equipment.putBoolean("ParkingBrake",false);car.loadEquipment(equipment);
            CompoundTag saved=new CompoundTag();car.saveWithoutId(saved);
            NgCityEntity restored=VehicleContent.type(spec).create(h.getLevel());restored.load(saved);
            h.assertTrue(restored.color()==DyeColor.RED.getId()&&restored.lightsOn()&&!restored.isParkingBraked(), "Equipment changed during NBT load");
            restored.tick();h.assertTrue(restored.isParkingBraked(), "Unoccupied loaded car did not apply automatic parking");
            h.assertTrue(car.getPickResult().getTag().getInt("VehicleColor")==DyeColor.RED.getId(), "Pickup lost paint");
            car.discard();
        }
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void occupiedVehicleRejectsOutsidePainting(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.CITY);enter(h,car);Player outsider=player(h);
        outsider.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.RED_DYE));
        h.assertFalse(car.interact(outsider,InteractionHand.MAIN_HAND).consumesAction(), "Outsider painted occupied vehicle");
        h.assertTrue(outsider.getMainHandItem().getCount()==1&&car.color()==VehicleSpec.CITY.defaultColor.getId(), "Rejected paint changed state or consumed dye");
        car.discard();h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void packedVanPlacesAsSameGroupWithSameEquipment(GameTestHelper h) {
        NgCityEntity van=car(h,VehicleSpec.TRANSPORTER);
        CompoundTag equipment=new CompoundTag();equipment.putInt("VehicleColor",DyeColor.BLUE.getId());equipment.putBoolean("VehicleLights",true);equipment.putBoolean("ParkingBrake",true);van.loadEquipment(equipment);
        ItemStack packed=van.getPickResult();BlockPos pos=van.blockPosition().below();van.discard();
        h.getLevel().setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());
        Player p=player(h);p.setItemInHand(InteractionHand.MAIN_HAND,packed);
        var context=new UseOnContext(h.getLevel(),p,InteractionHand.MAIN_HAND,packed,
                new BlockHitResult(Vec3.atBottomCenterOf(pos.above()),Direction.UP,pos,false));
        h.assertTrue(packed.getItem().useOn(context).consumesAction(), "Packed van did not place");
        var cars=h.getLevel().getEntitiesOfClass(NgCityEntity.class,new net.minecraft.world.phys.AABB(pos).inflate(4));
        h.assertTrue(cars.size()==1&&cars.get(0).spec()==VehicleSpec.TRANSPORTER&&cars.get(0).color()==DyeColor.BLUE.getId()&&cars.get(0).lightsOn(), "Re-placement lost van type/equipment");
        cars.forEach(NgCityEntity::discard);h.getLevel().removeBlock(pos,false);h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void legacyAndMalformedEquipmentUseSafeDefaults(GameTestHelper h) {
        NgCityEntity van=car(h,VehicleSpec.TRANSPORTER);van.loadEquipment(new CompoundTag());
        h.assertTrue(van.color()==DyeColor.WHITE.getId()&&van.isParkingBraked()&&!van.lightsOn(), "Legacy state did not receive defaults");
        CompoundTag invalid=new CompoundTag();invalid.putInt("VehicleColor",Integer.MAX_VALUE);van.loadEquipment(invalid);
        h.assertTrue(van.color()==DyeColor.WHITE.getId(), "Invalid color not normalized");
        van.discard();h.succeed();
    }
}
