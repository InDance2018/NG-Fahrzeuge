package de.nevergate.fahrzeuge.visualtest;
import de.nevergate.fahrzeuge.*;import de.nevergate.fahrzeuge.client.*;
import net.minecraft.client.Minecraft;import net.minecraft.client.Screenshot;
import net.minecraftforge.event.TickEvent;import net.minecraftforge.eventbus.api.SubscribeEvent;import net.minecraftforge.fml.common.Mod;import net.minecraftforge.api.distmarker.Dist;import net.minecraftforge.network.PacketDistributor;
@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID,value=Dist.CLIENT)
public final class Dev10SceneClient {
 static int ticks,dealerTicks,driveTicks;static boolean roof;
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){if(!Boolean.getBoolean("ng_fahrzeuge.dev10Visual")||e.phase!=TickEvent.Phase.END)return;var mc=Minecraft.getInstance();mc.options.pauseOnLostFocus=false;if(mc.player==null)return;ticks++;mc.options.fov().set(45);mc.options.renderDistance().set(5);mc.options.hideGui=mc.player.isSpectator();
  if(ticks==120){shot(mc,"hood-rack-dirt-tyre");camera(mc,44,143,-10,35,15);}
  if(ticks==190)shot(mc,"winch");if(ticks==260){shot(mc,"flatbed-beacon");camera(mc,16,140.8,-7,35,12);}
  if(ticks==320){shot(mc,"cargo-compartment");camera(mc,60,143,-8,30,18);}
  if(ticks==390)shot(mc,"trailers");
  if(mc.screen instanceof VehicleServiceScreen&&++dealerTicks==12){shot(mc,"dealer-sale");mc.gameMode.handleInventoryButtonClick(mc.player.containerMenu.containerId,4);}
  if(ticks==520&&mc.screen instanceof net.minecraft.client.gui.screens.inventory.ContainerScreen){shot(mc,"roof-cargo");roof=true;}
  if(ticks>550&&mc.player.getVehicle() instanceof NgCityEntity car&&car.getDriver()==mc.player){driveTicks++;if(driveTicks==1){send(car,VehicleNetwork.ENGINE_ACTION);send(car,VehicleNetwork.PARK_ACTION);}mc.options.keyUp.setDown(driveTicks<90);
   if(driveTicks==15)cameraKey();if(driveTicks==55){if(!VehicleCamera.following())throw new IllegalStateException("Follow camera not active");shot(mc,"follow-camera-hud");}
   if(driveTicks==90)cameraKey();if(driveTicks==92){if(VehicleCamera.following()||mc.getCameraEntity()!=mc.player)throw new IllegalStateException("Camera did not return");System.out.println("NG_DEV10_CAMERA_RETURN_PASS");}if(driveTicks==95)cameraKey();
  }else mc.options.keyUp.setDown(false);
  if(ticks>710){mc.options.keyUp.setDown(false);if(VehicleCamera.following()||mc.getCameraEntity()!=mc.player)throw new IllegalStateException("Camera did not restore on exit");System.out.println("NG_DEV10_CAMERA_EXIT_PASS");if(!roof||dealerTicks<12||driveTicks<60)throw new IllegalStateException("Incomplete visual check");System.out.println("NG_DEV10_VISUAL_COMPLETE");mc.stop();}
 }
 static void cameraKey(){net.minecraft.client.KeyMapping.click(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(org.lwjgl.glfw.GLFW.GLFW_KEY_F6));}
 static void send(NgCityEntity c,int action){VehicleNetwork.CHANNEL.send(new VehicleNetwork.Action(c.getId(),action),PacketDistributor.SERVER.noArg());}
 static void shot(Minecraft mc,String n){System.out.println("NG_DEV10_CAMERA_TRACE "+n+" player="+mc.player.position()+" cam="+mc.getCameraEntity()+" cameraPos="+mc.gameRenderer.getMainCamera().getPosition()+" entities="+java.util.stream.StreamSupport.stream(mc.level.entitiesForRendering().spliterator(),false).filter(x->x instanceof NgCityEntity).map(x->x.position().toString()).toList());Screenshot.grab(mc.gameDirectory,"dev10-"+n+".png",mc.getMainRenderTarget(),m->System.out.println("NG_DEV10_SCREENSHOT "+m.getString()));}
 static void camera(Minecraft mc,double x,double y,double z,float yaw,float pitch){var server=mc.getSingleplayerServer();var id=mc.player.getUUID();server.execute(()->{var p=server.getPlayerList().getPlayer(id);p.teleportTo(p.serverLevel(),x,y,z,yaw,pitch);});}
}
