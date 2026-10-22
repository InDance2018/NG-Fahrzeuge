package de.nevergate.fahrzeuge.visualtest;
import de.nevergate.fahrzeuge.*;
import de.nevergate.fahrzeuge.client.*;
import net.minecraft.client.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID,value=Dist.CLIENT)
public final class HudVisualScene {
 static int clientTicks;static boolean ready;
 @SubscribeEvent public static void server(TickEvent.ServerTickEvent e) {
  if(!Boolean.getBoolean("ng_fahrzeuge.hudVisual")||e.phase!=TickEvent.Phase.END||ready||e.getServer().getPlayerList().getPlayers().isEmpty())return;
  var p=e.getServer().getPlayerList().getPlayers().get(0);var l=p.serverLevel();
  l.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false,e.getServer());l.setDayTime(6000);l.setWeatherParameters(100000,0,false,false);
  for(int x=-15;x<=15;x++)for(int z=-10;z<=80;z++)l.setBlockAndUpdate(new BlockPos(x,79,z),Math.abs(x)==4&&z%5<3?Blocks.WHITE_CONCRETE.defaultBlockState():Blocks.GRAY_CONCRETE.defaultBlockState());
  p.setGameMode(GameType.CREATIVE);p.stopRiding();p.teleportTo(l,0,80,0,0,0);
  var car=VehicleContent.type(VehicleSpec.FAMILY).create(l);car.setPos(0,80,0);l.addFreshEntity(car);car.equipment.claim(p);p.startRiding(car);ready=true;
  System.out.println("HUD_VISUAL_SCENE_READY");
 }
 @SubscribeEvent public static void client(TickEvent.ClientTickEvent e) {
  if(!Boolean.getBoolean("ng_fahrzeuge.hudVisual")||e.phase!=TickEvent.Phase.END)return;
  var mc=Minecraft.getInstance();mc.options.pauseOnLostFocus=false;
  if(mc.player==null||!(mc.player.getVehicle() instanceof NgCityEntity))return;
  clientTicks++;
  if(clientTicks==5){mc.options.guiScale().set(4);mc.resizeDisplay();mc.options.setCameraType(CameraType.FIRST_PERSON);}
  if(clientTicks==50){check(!VehicleHudSettings.details(),"Default must be compact");shot(mc,"hudfix-compact-gui4.png");}
  if(clientTicks==65)click(DrivingClient.HUD_DETAILS);
  if(clientTicks==90){check(VehicleHudSettings.details(),"Details key failed");shot(mc,"hudfix-details-gui4.png");}
  if(clientTicks==105){mc.options.guiScale().set(2);mc.resizeDisplay();}
  if(clientTicks==130)shot(mc,"hudfix-details-gui2.png");
  if(clientTicks==145){click(DrivingClient.HUD_DETAILS);click(DrivingClient.HUD_SMALLER);}
  if(clientTicks==170){check(!VehicleHudSettings.details()&&VehicleHudSettings.scale()<1,"HUD resize/toggle failed");shot(mc,"hudfix-compact-small-gui2.png");}
  if(clientTicks==185)mc.options.hideGui=true;
  if(clientTicks==195)shot(mc,"hudfix-hidden.png");
  if(clientTicks==210){System.out.println("HUD_VISUAL_KEYS_AND_SCREENSHOTS_PASS");mc.stop();}
 }
 static void click(KeyMapping k){KeyMapping.click(com.mojang.blaze3d.platform.InputConstants.getKey(k.saveString()));}
 static void check(boolean ok,String message){if(!ok)throw new IllegalStateException(message);}
 static void shot(Minecraft mc,String name){Screenshot.grab(mc.gameDirectory,name,mc.getMainRenderTarget(),m->System.out.println("HUD_VISUAL_SCREENSHOT "+m.getString()));}
}
