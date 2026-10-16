package de.nevergate.fahrzeuge.visualtest;
import de.nevergate.fahrzeuge.*;import de.nevergate.fahrzeuge.client.*;
import net.minecraft.client.Minecraft;import net.minecraft.client.Screenshot;
import net.minecraftforge.event.TickEvent;import net.minecraftforge.eventbus.api.SubscribeEvent;import net.minecraftforge.fml.common.Mod;import net.minecraftforge.api.distmarker.Dist;import net.minecraftforge.network.PacketDistributor;
@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID,value=Dist.CLIENT)
public final class Dev9SceneClient {
 static int ticks,dealerTicks,garageTicks,manageTicks;static boolean stored,recalled;
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){if(!Boolean.getBoolean("ng_fahrzeuge.dev9Visual")||e.phase!=TickEvent.Phase.END)return;var mc=Minecraft.getInstance();mc.options.pauseOnLostFocus=false;if(mc.player==null)return;ticks++;mc.options.fov().set(45);mc.options.renderDistance().set(5);mc.options.hideGui=mc.player.isSpectator();
  if(ticks==120){shot(mc,"comfort-plate");camera(mc,17,142,7,145,14);}
  if(ticks==190){shot(mc,"family-roof");camera(mc,29,142,7,145,14);}
  if(ticks==260){shot(mc,"van-windows");camera(mc,44,143,-9,35,17);}
  if(ticks==325){shot(mc,"flatbed");camera(mc,58,144,7,140,18);}
  if(ticks==350)shot(mc,"lift");
  if(mc.screen instanceof VehicleServiceScreen&&mc.player.containerMenu instanceof VehicleServiceMenu menu){
   if(menu.service==0&&++dealerTicks==12){shot(mc,"dealer");mc.gameMode.handleInventoryButtonClick(menu.containerId,0);}
   if(menu.service==1){garageTicks++;if(!stored&&garageTicks==12){mc.gameMode.handleInventoryButtonClick(menu.containerId,9);stored=true;}
    if(stored&&!recalled&&garageTicks==85&&!menu.previews.isEmpty()){shot(mc,"garage");mc.gameMode.handleInventoryButtonClick(menu.containerId,0);recalled=true;}}
  }
  if(mc.screen instanceof VehicleManagementScreen&&++manageTicks==12){shot(mc,"management");var m=(VehicleManagementMenu)mc.player.containerMenu;VehicleNetwork.CHANNEL.send(new VehicleNetwork.Manage(m.carId,3,"NG DEV 9"),PacketDistributor.SERVER.noArg());}
  if(ticks>690){if(!stored||!recalled||manageTicks<12||dealerTicks<12)throw new IllegalStateException("Incomplete Dev9 visual test");System.out.println("NG_DEV9_VISUAL_COMPLETE");mc.stop();}
 }
 static void shot(Minecraft mc,String n){Screenshot.grab(mc.gameDirectory,"dev9-"+n+".png",mc.getMainRenderTarget(),m->System.out.println("NG_DEV9_SCREENSHOT "+m.getString()));}
 static void camera(Minecraft mc,double x,double y,double z,float yaw,float pitch){var server=mc.getSingleplayerServer();var id=mc.player.getUUID();server.execute(()->{var p=server.getPlayerList().getPlayer(id);p.teleportTo(p.serverLevel(),x,y,z,yaw,pitch);});}
}
