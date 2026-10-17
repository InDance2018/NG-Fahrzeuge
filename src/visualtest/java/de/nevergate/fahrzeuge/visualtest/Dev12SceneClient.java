package de.nevergate.fahrzeuge.visualtest;
import de.nevergate.fahrzeuge.*;import de.nevergate.fahrzeuge.client.*;
import net.minecraft.client.*;import net.minecraftforge.event.TickEvent;import net.minecraftforge.eventbus.api.SubscribeEvent;import net.minecraftforge.fml.common.Mod;import net.minecraftforge.api.distmarker.Dist;import net.minecraftforge.network.PacketDistributor;
@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID,value=Dist.CLIENT)
public final class Dev12SceneClient {
 static int ticks,diagnosis,management,workbench,cargo,remote,drive,offDrive;static boolean page2,assembly;
 @SubscribeEvent public static void tick(TickEvent.ClientTickEvent e){if(!Boolean.getBoolean("ng_fahrzeuge.dev12Visual")||e.phase!=TickEvent.Phase.END)return;var mc=Minecraft.getInstance();mc.options.pauseOnLostFocus=false;if(mc.player==null)return;ticks++;mc.options.fov().set(50);mc.options.renderDistance().set(5);mc.options.hideGui=mc.player.isSpectator();
  if(ticks==120)shot(mc,"offroad-doors");if(ticks==220)shot(mc,"sports-car");if(ticks==300)shot(mc,"taxi-free");if(ticks==365)shot(mc,"taxi-occupied");if(ticks==440)shot(mc,"semi-parked");if(ticks==525)shot(mc,"fifth-wheel");
  if(mc.screen instanceof VehicleDiagnosisScreen&&++diagnosis==12)shot(mc,"diagnosis");
  if(mc.screen instanceof VehicleManagementScreen){management++;if(management==10)for(var c:java.util.List.copyOf(mc.screen.children()))if(c instanceof net.minecraft.client.gui.components.Button b&&b.getMessage().getString().equals("Technik")){b.onPress();break;}if(management==20)shot(mc,"mechanics-menu");}
  if(mc.screen instanceof VehicleWorkbenchScreen){workbench++;if(workbench==8)for(var c:java.util.List.copyOf(mc.screen.children()))if(c instanceof net.minecraft.client.gui.components.Button b&&b.getMessage().getString().contains("Sattelzug")){b.onPress();break;}if(workbench==15){shot(mc,"workbench");mc.gameMode.handleInventoryButtonClick(mc.player.containerMenu.containerId,VehicleSpec.TRACTOR.ordinal());assembly=true;}}
  if(mc.screen instanceof TrailerCargoScreen&&mc.player.containerMenu instanceof TrailerCargoMenu menu){cargo++;if(menu.page==0&&cargo==10)mc.gameMode.handleInventoryButtonClick(menu.containerId,0);if(menu.page==1&&!page2&&menu.slots.get(53).getItem().is(net.minecraft.world.item.Items.DIAMOND)&&menu.slots.get(53).getItem().getCount()==9&&cargo>16){page2=true;shot(mc,"semi-cargo-page2");}}
  if(ticks>860&&ticks<940&&mc.player.getMainHandItem().is(VehicleContent.KEY.get())&&++remote==10)mc.gameMode.useItem(mc.player,net.minecraft.world.InteractionHand.MAIN_HAND);
  if(ticks>940&&mc.player.getVehicle() instanceof NgCityEntity c&&c.spec()==VehicleSpec.SPORT){drive++;if(drive==1)key(org.lwjgl.glfw.GLFW.GLFW_KEY_KP_7);if(drive==5){send(c,2);send(c,0);}if(drive==20||drive==42||drive==65)key(org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ADD);if(drive==15)key(org.lwjgl.glfw.GLFW.GLFW_KEY_F6);mc.options.keyUp.setDown(drive<115);mc.options.keyLeft.setDown(drive>50&&drive<68);if(drive==85)shot(mc,"manual-gears-rpm");}else{mc.options.keyUp.setDown(false);mc.options.keyLeft.setDown(false);}
  if(ticks>1080&&mc.player.getVehicle() instanceof NgCityEntity c&&c.spec()==VehicleSpec.OFFROAD){offDrive++;if(offDrive==5){send(c,2);send(c,0);}if(offDrive==15&&!VehicleCamera.following())key(org.lwjgl.glfw.GLFW.GLFW_KEY_F6);mc.options.keyUp.setDown(offDrive<110);if(offDrive==65)shot(mc,"low-range-hud");}
  if(ticks>1240){mc.options.keyUp.setDown(false);if(diagnosis<12||management<20||!assembly||!page2||drive<100||offDrive<100||remote<10)throw new IllegalStateException("Incomplete Dev12 scene");System.out.println("NG_DEV12_CLIENT_PAGE2_PASS");System.out.println("NG_DEV12_VISUAL_COMPLETE");mc.stop();}
 }
 static void key(int n){KeyMapping.click(com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(n));}
 static void send(NgCityEntity c,int a){VehicleNetwork.CHANNEL.send(new VehicleNetwork.Action(c.getId(),a),PacketDistributor.SERVER.noArg());}
 static void shot(Minecraft mc,String n){Screenshot.grab(mc.gameDirectory,"dev12-"+n+".png",mc.getMainRenderTarget(),m->System.out.println("NG_DEV12_SCREENSHOT "+m.getString()));}
}
