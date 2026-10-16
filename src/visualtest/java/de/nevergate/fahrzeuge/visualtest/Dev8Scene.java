package de.nevergate.fahrzeuge.visualtest;

import de.nevergate.fahrzeuge.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID)
public final class Dev8Scene {
    private static int ticks;
    private static NgCityEntity family;
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event){
        if(!Boolean.getBoolean("ng_fahrzeuge.dev8Visual")||event.phase!=TickEvent.Phase.END||event.getServer().getPlayerList().getPlayers().isEmpty())return;
        ticks++;var p=event.getServer().getPlayerList().getPlayers().get(0);var level=p.serverLevel();
        if(ticks==1){
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false,event.getServer());level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false,event.getServer());
            level.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(false,event.getServer());
        }
        if(ticks==40){
            p.stopRiding();p.setGameMode(GameType.CREATIVE);level.setDayTime(5000);
            var leftovers=level.getEntitiesOfClass(net.minecraft.world.entity.Entity.class,new net.minecraft.world.phys.AABB(-30,130,-30,40,150,100),e->e instanceof NgCityEntity||e instanceof TrailerEntity);
            System.out.println("NG_DEV8_FIXTURE_OLD_ENTITIES "+leftovers.size());for(var e:leftovers)e.discard();level.setWeatherParameters(100000,0,false,false);
            for(int x=-10;x<37;x++)for(int z=-10;z<=90;z++)level.setBlockAndUpdate(new BlockPos(x,139,z),Blocks.SMOOTH_STONE.defaultBlockState());
            for(int i=0;i<3;i++){
                var car=VehicleContent.type(VehicleSpec.values()[i]).create(level);car.setPos(i*12,140,0);level.addFreshEntity(car);car.equipment.claim(p);
                p.teleportTo(i*12+2,140,2);if(i!=1){car.systems.action(p,VehicleNetwork.DOOR_ACTION);car.systems.action(p,VehicleNetwork.HATCH_ACTION);}else family=car;
            }
            var trailer=VehicleContent.TRAILER.get().create(level);trailer.setPos(12,140,-4);level.addFreshEntity(trailer);trailer.claim(p);
            p.teleportTo(14,140,2);if(!family.systems.couple(p))throw new IllegalStateException("Trailer fixture coupling failed");
            var pos=new BlockPos(5,140,0);level.setBlockAndUpdate(pos,VehicleContent.PUMP.get().defaultBlockState());((FuelPumpEntity)level.getBlockEntity(pos)).fill(35);
            p.setGameMode(GameType.SPECTATOR);p.teleportTo(level,3.3,140.55,4.9,145,16);
            System.out.println("NG_DEV8_SCENE_READY");
        }
        if(ticks==300){p.setGameMode(GameType.CREATIVE);p.teleportTo(level,14,140,2,145,16);if(!VehicleManagement.open(family,p))throw new IllegalStateException("Management open failed");}
        if(ticks==370){
            if(family.getCustomName()==null||!family.getCustomName().getString().equals("NG Family Dev8"))throw new IllegalStateException("Client name change failed");
            System.out.println("NG_DEV8_CLIENT_MANAGEMENT_PASS");p.closeContainer();p.startRiding(family);
        }
        if(ticks>=380&&ticks%20==0){
            var t=family.systems.trailer();System.out.println("NG_DEV8_DRIVE_TRACE tick="+ticks+" engine="+family.engineRunning()+" park="+family.isParkingBraked()+" speed="+family.getDriveSpeed()+" car="+family.position()+" trailer="+(t==null?"null":t.position())+" collision="+(t!=null&&t.horizontalCollision)+" nearby="+(t==null?"null":level.getEntities(t,t.getBoundingBox().inflate(.7)).stream().map(e->e.getType()+" pos="+e.position()+" collides="+t.canCollideWith(e)).toList()));
        }
        if(ticks==465){
            if(!family.engineRunning()||family.getDriveSpeed()<=0||family.systems.cruise()<=0||family.systems.metres()<=0||family.systems.fuel()>=55)
                throw new IllegalStateException("Client cruise/fuel/odometer failed: engine="+family.engineRunning()+" speed="+family.getDriveSpeed()+" cruise="+family.systems.cruise()+" fuel="+family.systems.fuel()+" distance="+family.systems.metres()+" driver="+family.getDriver());
            if(family.systems.trailer()==null||family.systems.trailer().distanceToSqr(family)>36)throw new IllegalStateException("Trailer follow failed: car="+family.position()+" trailerId="+family.systems.trailerId()+" trailer="+(family.systems.trailer()==null?"unloaded":family.systems.trailer().position()));
            System.out.println("NG_DEV8_CLIENT_DRIVE_CRUISE_TRAILER_PASS");
        }
    }
}
