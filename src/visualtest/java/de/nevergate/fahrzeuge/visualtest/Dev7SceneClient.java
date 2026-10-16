package de.nevergate.fahrzeuge.visualtest;

import de.nevergate.fahrzeuge.*;
import de.nevergate.fahrzeuge.client.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID,value=Dist.CLIENT)
public final class Dev7SceneClient {
    private static int ticks,workbenchTicks,cargoTicks;
    private static boolean rearShot,workbenchShot,cargoShot,assembled,started;
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event){
        if(!Boolean.getBoolean("ng_fahrzeuge.dev7Visual")||event.phase!=TickEvent.Phase.END)return;
        var mc=Minecraft.getInstance();mc.options.pauseOnLostFocus=false;
        if(mc.player==null)return;ticks++;
        mc.options.fov().set(35);mc.options.renderDistance().set(5);mc.options.hideGui=mc.screen==null;
        if(ticks>100&&!rearShot&&mc.player.isSpectator()&&mc.level.getGameTime()%20<8){shot(mc,"family-hazards");rearShot=true;}
        if(mc.screen instanceof VehicleWorkbenchScreen screen){
            workbenchTicks++;
            if(workbenchTicks==1)screen.mouseClicked((screen.width-240)/2.0+195,(screen.height-208)/2.0+29,0);
            if(workbenchTicks==10){shot(mc,"workbench");workbenchShot=true;}
            if(workbenchTicks>=15&&!assembled){mc.gameMode.handleInventoryButtonClick(mc.player.containerMenu.containerId,2);assembled=true;}
        }
        if(workbenchShot&&mc.screen instanceof net.minecraft.client.gui.screens.inventory.ContainerScreen&&!cargoShot&&++cargoTicks>=10){shot(mc,"cargo");cargoShot=true;}
        if(mc.player.getVehicle() instanceof NgCityEntity car && car.getDriver()==mc.player && cargoShot){
            if(!started){
                VehicleNetwork.CHANNEL.send(new VehicleNetwork.Action(car.getId(),VehicleNetwork.ENGINE_ACTION),net.minecraftforge.network.PacketDistributor.SERVER.noArg());
                VehicleNetwork.CHANNEL.send(new VehicleNetwork.Action(car.getId(),VehicleNetwork.PARK_ACTION),net.minecraftforge.network.PacketDistributor.SERVER.noArg());
                VehicleNetwork.CHANNEL.send(new VehicleNetwork.Action(car.getId(),VehicleNetwork.HORN_ACTION),net.minecraftforge.network.PacketDistributor.SERVER.noArg());started=true;
            }
            mc.options.keyUp.setDown(true);
        }
        if(ticks>500){
            mc.options.keyUp.setDown(false);
            if(!rearShot||!workbenchShot||!cargoShot)throw new IllegalStateException("Missing Dev7 screenshots");
            System.out.println("NG_DEV7_VISUAL_COMPLETE");mc.stop();
        }
    }
    private static void shot(Minecraft mc,String name){
        Screenshot.grab(mc.gameDirectory,"dev7-"+name+".png",mc.getMainRenderTarget(),m->System.out.println("NG_DEV7_SCREENSHOT "+m.getString()));
    }
}
