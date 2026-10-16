package de.nevergate.fahrzeuge.visualtest;
import de.nevergate.fahrzeuge.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID)
public final class Dev11Scene {
 static int ticks;static NgCityEntity bus,pickup,bike,family;static BlockPos dealer=new BlockPos(15,140,7);
 @SubscribeEvent public static void tick(TickEvent.ServerTickEvent e){
  if(!Boolean.getBoolean("ng_fahrzeuge.dev11Visual")||e.phase!=TickEvent.Phase.END||e.getServer().getPlayerList().getPlayers().isEmpty())return;
  ticks++;var p=e.getServer().getPlayerList().getPlayers().get(0);var l=p.serverLevel();
  if(ticks==1)for(var rule:java.util.List.of(GameRules.RULE_DOMOBLOOT,GameRules.RULE_DOMOBSPAWNING,GameRules.RULE_DOFIRETICK))l.getGameRules().getRule(rule).set(false,e.getServer());
  if(ticks==40){p.stopRiding();p.setCamera(p);p.setGameMode(GameType.CREATIVE);for(var old:l.getEntitiesOfClass(net.minecraft.world.entity.Entity.class,new net.minecraft.world.phys.AABB(-30,130,-30,85,170,120),x->x instanceof NgCityEntity||x instanceof TrailerEntity))old.discard();
   for(int x=-10;x<50;x++)for(int z=-10;z<45;z++)for(int y=140;y<144;y++)l.setBlockAndUpdate(new BlockPos(x,y,z),Blocks.AIR.defaultBlockState());
   l.setDayTime(5000);l.setWeatherParameters(100000,0,false,false);for(int x=-12;x<75;x++)for(int z=-15;z<100;z++)l.setBlockAndUpdate(new BlockPos(x,139,z),Blocks.SMOOTH_STONE.defaultBlockState());
   bus=spawn(l,p,VehicleSpec.BUS,0,0);p.teleportTo(0,140,2);bus.systems.expansion.setRoute(p,"42","NeverGate Zentrum");
   pickup=spawn(l,p,VehicleSpec.PICKUP,12,0);p.teleportTo(12,140,2);for(int i=0;i<21;i++)pickup.equipment.cargo.setItem(i,new ItemStack(Items.IRON_INGOT,8));pickup.systems.action(p,9);pickup.systems.damage(45);pickup.systems.upgrades.tool(p,new ItemStack(VehicleContent.RACK.get()));for(int i=0;i<6;i++)pickup.systems.upgrades.roof.setItem(i,new ItemStack(Items.PAPER));
   bike=spawn(l,p,VehicleSpec.MOTORCYCLE,23,0);family=spawn(l,p,VehicleSpec.FAMILY,32,0);p.teleportTo(32,140,2);family.systems.upgrades.tool(p,new ItemStack(VehicleContent.RACK.get()));for(int i=0;i<11;i++)family.systems.upgrades.roof.setItem(i,new ItemStack(Items.PAPER));
   l.setBlockAndUpdate(dealer,VehicleContent.DEALER.get().defaultBlockState());l.setBlockAndUpdate(new BlockPos(17,140,7),VehicleContent.PAINT.get().defaultBlockState());
   view(p,7,143,11,148,15);System.out.println("NG_DEV11_SCENE_READY");}
  if(ticks==150){p.setGameMode(GameType.CREATIVE);p.teleportTo(0,140,2);bus.systems.action(p,8);bus.systems.action(p,9);view(p,9,143,4,112,14);}
  if(ticks==250)view(p,18,142,-6,45,12);
  if(ticks==350)view(p,27,141.8,4,135,15);
  if(ticks==450){p.setGameMode(GameType.CREATIVE);p.teleportTo(32,140,1);p.startRiding(family);family.applyAction(p,1);family.applyAction(p,1);family.applyAction(p,21);family.applyAction(p,23);family.applyAction(p,20);family.applyAction(p,3);p.stopRiding();view(p,36,142,5,142,12);}
  if(ticks==530){p.setGameMode(GameType.CREATIVE);p.teleportTo(0,140,2);p.setCamera(p);VehicleManagement.open(bus,p);}
  if(ticks==600){if(!bus.systems.expansion.route().equals("N11")||bus.systems.expansion.seatHeight()!=3)throw new IllegalStateException("Client management packets failed");System.out.println("NG_DEV11_CLIENT_MANAGE_PASS");p.closeContainer();p.teleportTo(15,140,8);VehicleServiceMenu.open(p,dealer,0);}
  if(ticks==660){p.closeContainer();VehicleDirectoryMenu.open(p,0);}
  if(ticks==730){p.closeContainer();p.teleportTo(23,140,1);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,ItemStack.EMPTY);bike.interact(p,net.minecraft.world.InteractionHand.MAIN_HAND);}
  if(ticks==850){if(!bike.engineRunning()||bike.systems.metres()<1||Math.abs(bike.getYRot())<3)throw new IllegalStateException("Client motorcycle drive/steer failed");System.out.println("NG_DEV11_CLIENT_BIKE_DRIVE_PASS");p.stopRiding();p.teleportTo(0,140,2);p.startRiding(bus);bus.systems.closeDoors();}
  if(ticks==910){if(!bus.systems.expansion.belted(p))throw new IllegalStateException("Client belt failed");System.out.println("NG_DEV11_CLIENT_BELT_PASS");}
 }
 static void view(net.minecraft.server.level.ServerPlayer p,double x,double y,double z,float yaw,float pitch){p.stopRiding();p.setCamera(p);p.setGameMode(GameType.SPECTATOR);p.teleportTo(p.serverLevel(),x,y,z,yaw,pitch);}
 static NgCityEntity spawn(net.minecraft.server.level.ServerLevel l,net.minecraft.server.level.ServerPlayer p,VehicleSpec s,double x,double z){var c=VehicleContent.type(s).create(l);c.setPos(x,140,z);l.addFreshEntity(c);c.equipment.claim(p);c.systems.comfort.setPlate("NG DEV 11");return c;}
}
