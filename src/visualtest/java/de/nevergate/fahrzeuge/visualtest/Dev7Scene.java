package de.nevergate.fahrzeuge.visualtest;

import de.nevergate.fahrzeuge.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Disposable Dev7 scene: real packets, real server menus, production renderers. */
@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID)
public final class Dev7Scene {
    private static int ticks;
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if(!Boolean.getBoolean("ng_fahrzeuge.dev7Visual")||event.phase!=TickEvent.Phase.END
                || event.getServer().getPlayerList().getPlayers().isEmpty())return;
        ticks++;
        var p=event.getServer().getPlayerList().getPlayers().get(0);var level=p.serverLevel();
        if(ticks==1){
            level.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false,event.getServer());
            level.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(false,event.getServer());
            level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false,event.getServer());
        }
        if(ticks==40){
            p.setGameMode(GameType.CREATIVE);level.setDayTime(5000);level.setWeatherParameters(100000,0,false,false);
            for(int i=0;i<3;i++){
                int x=i*48;
                for(int xx=x-9;xx<=x+9;xx++)for(int z=-10;z<=10;z++)level.setBlockAndUpdate(new BlockPos(xx,139,z),Blocks.SMOOTH_STONE.defaultBlockState());
                var car=VehicleContent.type(VehicleSpec.values()[i]).create(level);car.setPos(x,140,0);level.addFreshEntity(car);
                car.equipment.claim(p);p.startRiding(car);
                car.applyAction(p,VehicleNetwork.LIGHT_ACTION);car.applyAction(p,VehicleNetwork.HAZARD_ACTION);p.stopRiding();
            }
            p.setGameMode(GameType.SPECTATOR);p.teleportTo(level,51.3,140.55,-4.9,35,16);
            System.out.println("NG_DEV7_SCENE_READY");
        }
        if(ticks==210){
            p.setGameMode(GameType.SURVIVAL);p.teleportTo(level,2,140,2,145,16);
            BlockPos pos=new BlockPos(3,140,2);level.setBlockAndUpdate(pos,VehicleContent.WORKBENCH.get().defaultBlockState());
            p.getInventory().clearContent();for(var cost:VehicleAssembly.costs(VehicleSpec.TRANSPORTER))p.getInventory().add(new ItemStack(cost.item(),cost.count()));
            p.openMenu(new SimpleMenuProvider((id,inventory,who)->new VehicleWorkbenchMenu(id,inventory,pos),Component.translatable("block.ng_fahrzeuge.vehicle_workbench")));
            System.out.println("NG_DEV7_WORKBENCH_OPEN");
        }
        if(ticks==340){
            boolean built=p.getInventory().countItem(VehicleContent.NG_TRANSPORTER_ITEM.get())==1;
            if(!built)throw new IllegalStateException("Workbench client transaction failed");
            System.out.println("NG_DEV7_CLIENT_ASSEMBLY_PASS");p.closeContainer();p.teleportTo(level,96.8,140,2,145,16);
            var cars=level.getEntitiesOfClass(NgCityEntity.class,p.getBoundingBox().inflate(8));var car=cars.get(0);
            car.equipment.cargo.setItem(0,new ItemStack(Items.IRON_INGOT,32));car.equipment.cargo.setItem(53,new ItemStack(Items.EMERALD,5));
            if(!car.equipment.open(p))throw new IllegalStateException("Cargo menu rejected");
        }
        if(ticks==400){
            p.closeContainer();p.teleportTo(level,48.8,140,2,0,0);
            var car=level.getEntitiesOfClass(NgCityEntity.class,p.getBoundingBox().inflate(8)).get(0);
            p.startRiding(car);
        }
        if(ticks==460){
            var car=(NgCityEntity)p.getVehicle();
            if(car==null||!car.engineRunning()||car.getDriveSpeed()<=0)throw new IllegalStateException("Client engine/drive packet test failed");
            System.out.println("NG_DEV7_CLIENT_ENGINE_DRIVE_PASS");
        }

    }
}
