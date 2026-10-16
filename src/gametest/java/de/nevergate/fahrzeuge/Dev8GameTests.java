package de.nevergate.fahrzeuge;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(value=NgFahrzeuge.MOD_ID,namespace=NgFahrzeuge.MOD_ID)
public final class Dev8GameTests {
    private static NgCityEntity car(GameTestHelper h,VehicleSpec spec){
        var car=VehicleContent.type(spec).create(h.getLevel());var at=h.absolutePos(new BlockPos(1,1,1));
        car.setPos(at.getX()+.5,h.getLevel().getMaxBuildHeight()-8,at.getZ()+.5);car.setNoGravity(true);h.getLevel().addFreshEntity(car);return car;
    }
    private static Player player(GameTestHelper h,NgCityEntity car){var p=h.makeMockPlayer();p.setUUID(UUID.randomUUID());p.getAbilities().mayBuild=true;p.getAbilities().instabuild=false;p.setPos(car.position());return p;}
    private static Player driver(GameTestHelper h,NgCityEntity car){var p=player(h,car);car.interact(p,InteractionHand.MAIN_HAND);return p;}
    private static void drive(NgCityEntity car,Player p){car.applyAction(p,VehicleNetwork.ENGINE_ACTION);car.applyAction(p,VehicleNetwork.PARK_ACTION);car.acceptInput(p,VehicleNetwork.FORWARD);}
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void actualTravelCountsMetresAndTripPersistsAcrossPacking(GameTestHelper h){
        var car=car(h,VehicleSpec.CITY);var p=driver(h,car);drive(car,p);double z=car.getZ();for(int i=0;i<12;i++)car.tick();
        h.assertTrue(car.systems.metres()>0&&Math.abs(car.systems.metres()-(car.getZ()-z))<.00001,"Odometer not based on actual movement");
        var loaded=VehicleContent.NG_CITY.get().create(h.getLevel());loaded.loadEquipment(car.getPickResult().getTag());
        h.assertTrue(loaded.systems.metres()==car.systems.metres()&&loaded.systems.trip()==car.systems.trip(),"Packing lost distances");
        p.stopRiding();p.setPos(car.position());h.assertTrue(car.systems.action(p,VehicleNetwork.TRIP_ACTION)&&car.systems.trip()==0&&car.systems.metres()>0,"Trip reset changed total");car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void emptyFuelRejectsEngineAndFuelUsageVariesByVehicle(GameTestHelper h){
        for(var spec:VehicleSpec.values()){
            var car=car(h,spec);var p=driver(h,car);CompoundTag tag=new CompoundTag();car.systems.save(tag);tag.putFloat("Fuel",0);car.systems.load(tag);
            h.assertFalse(car.applyAction(p,VehicleNetwork.ENGINE_ACTION),"Empty tank started");
            p.stopRiding();p.setPos(car.position());h.assertTrue(car.systems.refuel(10)==10,"Refuel failed");
            p.startRiding(car);drive(car,p);for(int i=0;i<10;i++)car.tick();
            h.assertTrue(car.systems.fuel()<10&&car.systems.fuel()>0,"Fuel was not consumed");car.discard();
        }h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void partialFuelCanRetainsRemainderAndPumpConservesLitres(GameTestHelper h){
        var car=car(h,VehicleSpec.CITY);var p=player(h,car);car.equipment.claim(p);
        var tag=new CompoundTag();car.systems.save(tag);tag.putFloat("Fuel",38);car.systems.load(tag);
        var can=new ItemStack(VehicleContent.FUEL_CAN.get());h.assertTrue(FuelCanItem.refuel(car,p,can),"Can failed");
        h.assertTrue(car.systems.fuel()==40&&FuelCanItem.litres(can)==8,"Partial can lost/created fuel");
        BlockPos pos=car.blockPosition().offset(2,0,0);h.getLevel().setBlockAndUpdate(pos,VehicleContent.PUMP.get().defaultBlockState());
        var pump=(FuelPumpEntity)h.getLevel().getBlockEntity(pos);pump.fill(20);
        tag.putFloat("Fuel",35);car.systems.load(tag);h.assertTrue(pump.dispense(car,p)==5&&pump.litres()==15,"Pump did not transfer exact remainder");
        var saved=pump.saveWithoutMetadata();var loaded=new FuelPumpEntity(pos,VehicleContent.PUMP.get().defaultBlockState());loaded.load(saved);
        h.assertTrue(loaded.litres()==15,"Pump stock lost on NBT load");car.discard();h.getLevel().removeBlock(pos,false);h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void damageStopsBrokenEngineAndRepairConsumesPartAndToolDurability(GameTestHelper h){
        var car=car(h,VehicleSpec.CITY);var p=driver(h,car);drive(car,p);car.systems.damage(250);
        h.assertTrue(!car.engineRunning()&&car.systems.body()==0&&car.systems.motor()==0,"Destroyed state still drives");
        p.stopRiding();p.setPos(car.position());var tool=new ItemStack(VehicleContent.REPAIR_TOOL.get());
        h.assertFalse(car.systems.repair(p,tool),"Free survival repair succeeded");p.getInventory().setItem(8,new ItemStack(VehicleContent.PARTS.get(),2));
        h.assertTrue(car.systems.repair(p,tool)&&car.systems.body()==25&&car.systems.motor()==25,"Repair did not restore components");
        h.assertTrue(tool.getDamageValue()==1&&p.getInventory().getItem(8).getCount()==1,"Repair did not consume exact cost");car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void materialGripChangesBrakingAndSteeringLimits(GameTestHelper h){
        var car=car(h,VehicleSpec.CITY);var pos=car.blockPosition().below();
        h.getLevel().setBlockAndUpdate(pos,Blocks.STONE.defaultBlockState());float road=car.systems.grip();float a=car.systems.speed(.2F,0,true,false);
        h.getLevel().setBlockAndUpdate(pos,Blocks.ICE.defaultBlockState());float ice=car.systems.grip();float b=car.systems.speed(.2F,0,true,false);
        h.assertTrue(ice<road&&b>a,"Ice did not lengthen braking");h.getLevel().setBlockAndUpdate(pos,Blocks.SNOW_BLOCK.defaultBlockState());
        h.assertTrue(car.systems.grip()>ice&&car.systems.grip()<road,"Snow grip not between ice and road");car.discard();h.getLevel().removeBlock(pos,false);h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void cruiseMaintainsSpeedAndBrakeCancelsIt(GameTestHelper h){
        var car=car(h,VehicleSpec.CITY);var p=driver(h,car);drive(car,p);for(int i=0;i<12;i++)car.tick();
        h.assertTrue(car.applyAction(p,VehicleNetwork.CRUISE_ACTION),"Cruise activation failed");float speed=car.getDriveSpeed();car.acceptInput(p,0);
        for(int i=0;i<8;i++)car.tick();h.assertTrue(Math.abs(car.getDriveSpeed()-speed)<.001&&car.systems.cruise()>0,"Cruise did not hold speed");
        car.acceptInput(p,VehicleNetwork.BRAKE);car.tick();h.assertTrue(car.systems.cruise()==0&&car.getDriveSpeed()<speed,"Brake did not cancel cruise");car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void doorsRequireParkedEngineOffAndEngineStartClosesThem(GameTestHelper h){
        var car=car(h,VehicleSpec.FAMILY);var p=driver(h,car);
        h.assertTrue(car.applyAction(p,VehicleNetwork.DOOR_ACTION)&&car.systems.doors()==1,"Doors did not open");
        car.applyAction(p,VehicleNetwork.HATCH_ACTION);h.assertTrue(car.systems.doors()==3,"Rear did not open");
        car.applyAction(p,VehicleNetwork.ENGINE_ACTION);h.assertTrue(car.systems.doors()==0,"Motor start did not close doors");
        h.assertFalse(car.applyAction(p,VehicleNetwork.DOOR_ACTION),"Doors opened with engine running");car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void keyRevocationAndOwnershipTransferInvalidateOldKeys(GameTestHelper h){
        var car=car(h,VehicleSpec.CITY);var p=player(h,car);var other=player(h,car);car.equipment.claim(p);
        var key=new ItemStack(VehicleContent.KEY.get());car.equipment.bindKey(p,key);other.getInventory().setItem(8,key);
        h.assertTrue(car.equipment.authorized(other),"Shared key fixture failed");
        h.assertTrue(car.equipment.revokeKeys(p)&&!car.equipment.authorized(other),"Lost key still works");
        h.assertFalse(car.equipment.changeOwner(other,other.getUUID()),"Stranger transferred ownership");
        h.assertTrue(car.equipment.changeOwner(p,other.getUUID())&&car.equipment.isOwner(other)&&!car.equipment.authorized(p),"Ownership transfer failed");car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void trailerCouplesFollowsPersistsAndBlocksPickupWhileAttached(GameTestHelper h){
        var car=car(h,VehicleSpec.FAMILY);var p=player(h,car);car.equipment.claim(p);
        var t=VehicleContent.TRAILER.get().create(h.getLevel());t.setNoGravity(true);t.setPos(car.getX(),car.getY(),car.getZ()-4);t.claim(p);h.getLevel().addFreshEntity(t);
        t.cargo.setItem(26,new ItemStack(Items.DIAMOND,3));h.assertTrue(car.systems.couple(p),"Coupling failed");
        h.assertTrue(t.towId().equals(car.getUUID())&&car.systems.attached(),"Bidirectional coupling missing");
        p.setShiftKeyDown(true);car.interact(p,InteractionHand.MAIN_HAND);h.assertFalse(car.isRemoved(),"Coupled car was packed");
        double old=t.getZ();car.setPos(car.getX(),car.getY(),car.getZ()+.5);t.tick();h.assertTrue(t.getZ()>old,"Trailer did not follow");
        var saved=new CompoundTag();t.saveWithoutId(saved);var loaded=VehicleContent.TRAILER.get().create(h.getLevel());loaded.load(saved);
        h.assertTrue(loaded.towId().equals(car.getUUID())&&loaded.cargo.getItem(26).getCount()==3,"Trailer save lost coupling/cargo");
        p.setPos(car.position());h.assertTrue(car.systems.couple(p)&&t.towId()==null&&!car.systems.attached(),"Uncoupling failed");car.discard();t.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void trailerRejectsStrangerCityAndSecondTowVehicle(GameTestHelper h){
        var car=car(h,VehicleSpec.CITY);var p=player(h,car);car.equipment.claim(p);
        var t=VehicleContent.TRAILER.get().create(h.getLevel());t.setPos(car.getX(),car.getY(),car.getZ()-4);t.claim(p);t.setNoGravity(true);h.getLevel().addFreshEntity(t);
        h.assertFalse(car.systems.couple(p),"City coupled despite lacking tow capacity");
        var other=player(h,car);h.assertFalse(t.authorized(other),"Stranger accessed trailer");
        car.discard();t.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void malformedAndLegacySystemsUseFiniteSafeDefaults(GameTestHelper h){
        var car=car(h,VehicleSpec.TRANSPORTER);var tag=new CompoundTag();tag.putFloat("Fuel",Float.NaN);tag.putDouble("Metres",Double.POSITIVE_INFINITY);tag.putFloat("Motor",-999);tag.putFloat("Body",999);
        car.systems.load(tag);h.assertTrue(Float.isFinite(car.systems.fuel())&&car.systems.metres()==0&&car.systems.motor()==0&&car.systems.body()==100,"Malformed systems escaped bounds");
        car.systems.load(new CompoundTag());h.assertTrue(car.systems.fuel()==80&&car.systems.motor()==100&&car.systems.body()==100,"Legacy defaults failed");car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void managementRequiresOwnerParkedNearbyAndRejectsForgedContext(GameTestHelper h){
        var car=car(h,VehicleSpec.CITY);var p=player(h,car);var other=player(h,car);car.equipment.claim(p);
        var menu=new VehicleManagementMenu(7,p.getInventory(),car.getId());h.assertTrue(menu.stillValid(p),"Owner management rejected");
        h.assertFalse(menu.stillValid(other),"Stranger management accepted");
        h.assertFalse(VehicleManagement.edit(p,car.getId(),0,"Forged"),"Edit without matching open menu accepted");
        p.setPos(car.getX()+20,car.getY(),car.getZ());h.assertFalse(menu.stillValid(p),"Remote management accepted");car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void hundredTickTrailerRunStaysCoupledAndCopiesLights(GameTestHelper h){
        var car=car(h,VehicleSpec.FAMILY);var p=driver(h,car);
        var t=VehicleContent.TRAILER.get().create(h.getLevel());t.setNoGravity(true);t.setPos(car.getX(),car.getY(),car.getZ()-4);t.claim(p);h.getLevel().addFreshEntity(t);
        h.assertTrue(car.systems.couple(p),"Initial coupling failed");drive(car,p);car.applyAction(p,VehicleNetwork.LIGHT_ACTION);
        for(int i=0;i<100;i++){car.tick();t.tick();}
        h.assertTrue(car.systems.trailer()==t&&car.distanceToSqr(t)<20&&t.lights(),"Long trailer run detached or lost lights");
        car.acceptInput(p,VehicleNetwork.BRAKE);car.tick();t.tick();h.assertTrue(t.braking(),"Trailer did not copy brake light");car.discard();t.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void iceBrakingEventuallyReachesExactRest(GameTestHelper h){
        var car=car(h,VehicleSpec.CITY);var pos=car.blockPosition().below();h.getLevel().setBlockAndUpdate(pos,Blocks.ICE.defaultBlockState());float speed=.2F;
        for(int i=0;i<100;i++)speed=car.systems.speed(speed,0,true,false);
        h.assertTrue(speed==0,"Ice brake kept drifting forever");car.discard();h.getLevel().removeBlock(pos,false);h.succeed();
    }

    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void actualWallImpactDamagesBodyAndMotor(GameTestHelper h){
        var car=car(h,VehicleSpec.CITY);var p=driver(h,car);var base=car.blockPosition().offset(0,0,3);
        for(int x=-2;x<=2;x++)for(int y=0;y<4;y++)h.getLevel().setBlockAndUpdate(base.offset(x,y,0),Blocks.STONE.defaultBlockState());
        drive(car,p);for(int i=0;i<40;i++)car.tick();
        h.assertTrue(car.systems.body()<100&&car.systems.motor()<100&&car.getDriveSpeed()==0,"Actual impact did not damage vehicle and stop it");
        car.discard();for(int x=-2;x<=2;x++)for(int y=0;y<4;y++)h.getLevel().removeBlock(base.offset(x,y,0),false);h.succeed();
    }

}
