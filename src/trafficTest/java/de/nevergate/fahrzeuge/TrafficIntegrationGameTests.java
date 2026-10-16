package de.nevergate.fahrzeuge;

import de.nevergate.verkehr.NgTraffic;
import de.nevergate.verkehr.RoadSystemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;

/** Compiled only with -PtrafficIntegrationDir; never part of the vehicle JAR. */
@GameTestHolder(value=NgFahrzeuge.MOD_ID,namespace=NgFahrzeuge.MOD_ID)
public final class TrafficIntegrationGameTests {
    private static RoadSystemEntity node(GameTestHelper h,int x,int z,Block block) {
        BlockPos p=h.absolutePos(new BlockPos(x,1,z));
        h.getLevel().setBlockAndUpdate(p.below(),Blocks.STONE.defaultBlockState());
        h.getLevel().setBlockAndUpdate(p,block.defaultBlockState());
        return (RoadSystemEntity)h.getLevel().getBlockEntity(p);
    }
    private static RoadSystemEntity zone(GameTestHelper h,int capacity,int occupied) {
        var c=node(h,5,1,NgTraffic.ROAD_CONTROLLER.get());var d=c.data();
        d.capacity=capacity;d.occupied=occupied;c.configure(d);return c;
    }
    private static NgCityEntity car(GameTestHelper h,VehicleSpec spec,double x,double y,double z) {
        var car=VehicleContent.type(spec).create(h.getLevel());car.setNoGravity(true);
        car.setPos(x,y,z);h.getLevel().addFreshEntity(car);return car;
    }

    @GameTest(template="ng_verkehr:empty",timeoutTicks=20)
    public static void trafficRecognizesAllParkedGroupsAndExcludesRiders(GameTestHelper h) {
        h.assertTrue(ModList.get().isLoaded("ng_verkehr"),"Integration run has no NG Verkehr");
        var pos=h.absolutePos(new BlockPos(2,2,2));
        for(VehicleSpec spec:VehicleSpec.values()) {
            var car=car(h,spec,pos.getX()+.5,pos.getY(),pos.getZ()+.5);
            h.assertTrue(RoadSystemEntity.vehicle(car),"Unoccupied vehicle group not recognized: "+spec);
            for(int i=0;i<spec.seats;i++) {
                var p=h.makeMockPlayer();p.getAbilities().mayBuild=true;
                h.assertTrue(car.interact(p,InteractionHand.MAIN_HAND).consumesAction(),"Integration rider could not enter");
                h.assertFalse(RoadSystemEntity.vehicle(p),"Rider counted as an independent vehicle");
            }
            h.assertTrue(RoadSystemEntity.vehicle(car),"Occupied vehicle group not recognized");
            car.discard();
        }
        h.succeed();
    }

    private static void space(GameTestHelper h,VehicleSpec spec,boolean riders) {
        var c=zone(h,10,0);var sensor=node(h,2,2,NgTraffic.SPACE_SENSOR.get());c.attach(sensor);
        var d=c.data();d.counting=1;c.configure(d);var p=sensor.getBlockPos();
        var car=car(h,spec,p.getX()+.5,p.getY()+.2,p.getZ()+.5);
        if(riders)for(int i=0;i<spec.seats;i++) {
            var rider=h.makeMockPlayer();rider.getAbilities().mayBuild=true;
            car.interact(rider,InteractionHand.MAIN_HAND);
        }
        h.runAfterDelay(12,()->{
            h.assertTrue(c.data().capacity==1&&c.data().occupied==1&&c.data().free()==0,
                    "Space occupancy wrong for "+spec+": "+c.data().occupied);
            car.discard();
        });
        h.runAfterDelay(28,()->{
            h.assertTrue(c.data().occupied==0&&c.data().free()==1,"Space did not release for "+spec);
            h.succeed();
        });
    }
    @GameTest(template="ng_verkehr:empty",timeoutTicks=45)
    public static void cityOccupiesAndReleasesAnActualParkingSpace(GameTestHelper h){space(h,VehicleSpec.CITY,false);}
    @GameTest(template="ng_verkehr:empty",timeoutTicks=45)
    public static void fourPersonFamilyCountsAsOneParkingSpace(GameTestHelper h){space(h,VehicleSpec.FAMILY,true);}
    @GameTest(template="ng_verkehr:empty",timeoutTicks=45)
    public static void unoccupiedTransporterUsesActualSpaceSensor(GameTestHelper h){space(h,VehicleSpec.TRANSPORTER,false);}

    private static void crossing(GameTestHelper h,boolean exit) {
        var c=zone(h,10,exit?1:0);var sensor=node(h,2,2,exit?NgTraffic.EXIT_SENSOR.get():NgTraffic.ENTRY_SENSOR.get());c.attach(sensor);
        var p=sensor.getBlockPos();var car=car(h,VehicleSpec.CITY,p.getX()+.5,p.getY()+.2,p.getZ()-.25);
        h.runAfterDelay(2,()->{car.setPos(p.getX()+.5,p.getY()+.2,p.getZ()-.25);sensor.tick();});
        h.runAfterDelay(3,()->{
            car.setPos(p.getX()+.5,p.getY()+.2,p.getZ()+1.25);sensor.tick();
            h.assertTrue(c.data().occupied==(exit?0:1),"Actual passage sensor did not count correctly");
            car.discard();h.succeed();
        });
    }
    @GameTest(template="ng_verkehr:empty",timeoutTicks=20)
    public static void actualEntranceCountsNgCity(GameTestHelper h){crossing(h,false);}
    @GameTest(template="ng_verkehr:empty",timeoutTicks=20)
    public static void actualExitReleasesNgCity(GameTestHelper h){crossing(h,true);}

    @GameTest(template="ng_verkehr:empty",timeoutTicks=45)
    public static void twoMeterBarrierAcceptsCity(GameTestHelper h) {
        var c=zone(h,2,0);var gate=node(h,2,2,NgTraffic.PARKING_BARRIER.get());c.attach(gate);
        var d=gate.data();d.heightCm=200;gate.configure(d);var p=gate.getBlockPos();
        var car=car(h,VehicleSpec.CITY,p.getX()+.5,p.getY()+.2,p.getZ()-2.6);
        h.runAfterDelay(10,()->{h.assertTrue(gate.barrierOpen(),"Fitting City did not open gate");car.discard();h.succeed();});
    }
    @GameTest(template="ng_verkehr:empty",timeoutTicks=45)
    public static void transporterIsBlockedAtTwoMetersAndAcceptedAtTwoPointSix(GameTestHelper h) {
        var c=zone(h,2,0);var gate=node(h,2,2,NgTraffic.PARKING_BARRIER.get());c.attach(gate);
        var d=gate.data();d.heightCm=200;gate.configure(d);var p=gate.getBlockPos();
        var car=car(h,VehicleSpec.TRANSPORTER,p.getX()+.5,p.getY()+.2,p.getZ()-2.6);
        h.runAfterDelay(10,()->{
            h.assertFalse(gate.barrierOpen(),"Oversized transporter opened a 2m entrance");
            var raised=gate.data();raised.heightCm=260;gate.configure(raised);
        });
        h.runAfterDelay(24,()->{h.assertTrue(gate.barrierOpen(),"Transporter rejected after height raised to 2.6m");car.discard();h.succeed();});
    }
    @GameTest(template="ng_verkehr:empty",timeoutTicks=30)
    public static void fullParkingZoneKeepsGateClosedForFittingCity(GameTestHelper h) {
        var c=zone(h,2,2);var gate=node(h,2,2,NgTraffic.PARKING_BARRIER.get());c.attach(gate);
        var d=gate.data();d.heightCm=200;gate.configure(d);var p=gate.getBlockPos();
        var car=car(h,VehicleSpec.CITY,p.getX()+.5,p.getY()+.2,p.getZ()-2.6);
        h.runAfterDelay(10,()->{h.assertFalse(gate.barrierOpen(),"Full zone admitted another vehicle");car.discard();h.succeed();});
    }
}
