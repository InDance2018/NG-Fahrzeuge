package de.nevergate.fahrzeuge.visualtest;

import de.nevergate.fahrzeuge.*;
import de.nevergate.fahrzeuge.client.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID,value=Dist.CLIENT)
public final class Dev8SceneClient {
    private static int ticks,menuTicks,driveTicks;
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
        if(!Boolean.getBoolean("ng_fahrzeuge.dev8Visual")||event.phase!=TickEvent.Phase.END)return;
        var mc=Minecraft.getInstance();mc.options.pauseOnLostFocus=false;if(mc.player==null)return;ticks++;
        mc.options.fov().set(40);mc.options.renderDistance().set(5);mc.options.hideGui=mc.player.isSpectator();
        if(ticks==120){shot(mc,"city-doors");camera(mc,28.5,141.4,-6.8,35,16);}
        if(ticks==190){shot(mc,"van-rear");camera(mc,16.5,141.9,-7.5,35,16);}
        if(ticks==260)shot(mc,"trailer-pump");
        if(mc.screen instanceof VehicleManagementScreen&&++menuTicks==12){
            shot(mc,"management");var menu=(VehicleManagementMenu)mc.player.containerMenu;
            VehicleNetwork.CHANNEL.send(new VehicleNetwork.Manage(menu.carId,0,"NG Family Dev8"),PacketDistributor.SERVER.noArg());
        }
        if(menuTicks>=12&&mc.player.getVehicle() instanceof NgCityEntity car&&car.getDriver()==mc.player){
            driveTicks++;
            if(driveTicks==1){send(car,VehicleNetwork.ENGINE_ACTION);send(car,VehicleNetwork.PARK_ACTION);}
            mc.options.keyUp.setDown(driveTicks<20);
            if(driveTicks==20)send(car,VehicleNetwork.CRUISE_ACTION);
            if(driveTicks==60)shot(mc,"hud");
        }
        if(ticks>530){mc.options.keyUp.setDown(false);if(menuTicks<12||driveTicks<60)throw new IllegalStateException("Incomplete Dev8 client test");System.out.println("NG_DEV8_VISUAL_COMPLETE");mc.stop();}
    }
    private static void send(NgCityEntity car,int action){VehicleNetwork.CHANNEL.send(new VehicleNetwork.Action(car.getId(),action),PacketDistributor.SERVER.noArg());}
    private static void camera(Minecraft mc,double x,double y,double z,float yaw,float pitch){
        var server=mc.getSingleplayerServer();var id=mc.player.getUUID();server.execute(()->{var p=server.getPlayerList().getPlayer(id);p.teleportTo(p.serverLevel(),x,y,z,yaw,pitch);});
    }
    private static void shot(Minecraft mc,String name){Screenshot.grab(mc.gameDirectory,"dev8-"+name+".png",mc.getMainRenderTarget(),m->System.out.println("NG_DEV8_SCREENSHOT "+m.getString()));}
}
