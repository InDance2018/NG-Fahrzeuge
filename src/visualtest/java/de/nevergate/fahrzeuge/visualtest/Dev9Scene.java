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
public final class Dev9Scene {
 static int ticks;static NgCityEntity city,van,truck,family,liftCar;static BlockPos dealer=new BlockPos(3,140,7),garage=new BlockPos(18,140,7);
 @SubscribeEvent public static void tick(TickEvent.ServerTickEvent e){
  if(!Boolean.getBoolean("ng_fahrzeuge.dev9Visual")||e.phase!=TickEvent.Phase.END||e.getServer().getPlayerList().getPlayers().isEmpty())return;
  ticks++;var p=e.getServer().getPlayerList().getPlayers().get(0);var l=p.serverLevel();
  if(ticks==1)for(var rule:java.util.List.of(GameRules.RULE_DOMOBLOOT,GameRules.RULE_DOMOBSPAWNING,GameRules.RULE_DOFIRETICK))l.getGameRules().getRule(rule).set(false,e.getServer());
  if(ticks==40){
   p.stopRiding();p.setGameMode(GameType.CREATIVE);for(var old:l.getEntitiesOfClass(net.minecraft.world.entity.Entity.class,new net.minecraft.world.phys.AABB(-30,130,-30,80,160,100),x->x instanceof NgCityEntity||x instanceof TrailerEntity))old.discard();
   l.setDayTime(5000);l.setWeatherParameters(100000,0,false,false);for(int x=-10;x<65;x++)for(int z=-15;z<30;z++)l.setBlockAndUpdate(new BlockPos(x,139,z),Blocks.SMOOTH_STONE.defaultBlockState());
   city=spawn(l,p,VehicleSpec.CITY,0,0);family=spawn(l,p,VehicleSpec.FAMILY,12,0);family.systems.comfort.setVariant(1);van=spawn(l,p,VehicleSpec.TRANSPORTER,24,0);van.systems.comfort.setVariant(1);truck=spawn(l,p,VehicleSpec.TOW_TRUCK,38,0);
   var load=spawn(l,p,VehicleSpec.CITY,38,-6);p.teleportTo(38,140,-2);if(!VehicleRecovery.connect(truck,load,p,2))throw new IllegalStateException("Flatbed failed");truck.systems.comfort.toggleRamp();
   liftCar=spawn(l,p,VehicleSpec.CITY,52,0);var liftPos=new BlockPos(54,140,0);l.removeBlock(liftPos,false);l.setBlockAndUpdate(liftPos,VehicleContent.LIFT.get().defaultBlockState());p.teleportTo(52,140,3);if(!((VehicleLiftEntity)l.getBlockEntity(liftPos)).use(p))throw new IllegalStateException("Lift failed");
   l.setBlockAndUpdate(dealer,VehicleContent.DEALER.get().defaultBlockState());l.setBlockAndUpdate(garage,VehicleContent.GARAGE.get().defaultBlockState());
   p.teleportTo(0,140,2);city.systems.comfort.action(p,VehicleNetwork.WIPER_ACTION);city.systems.comfort.action(p,VehicleNetwork.WINDOW_ACTION);city.systems.comfort.action(p,VehicleNetwork.CABIN_ACTION);
   p.setGameMode(GameType.SPECTATOR);p.teleportTo(l,4,141.4,6,145,14);System.out.println("NG_DEV9_SCENE_READY");
  }
  if(ticks==360){p.setGameMode(GameType.CREATIVE);p.teleportTo(3,140,8);VehicleServiceMenu.open(p,dealer,0);}
  if(ticks==430){if(p.getInventory().countItem(VehicleContent.NG_CITY_ITEM.get())<1)throw new IllegalStateException("Client dealer purchase failed");p.closeContainer();p.teleportTo(18,140,6);family.setPos(18,140,2);VehicleServiceMenu.open(p,garage,1);}
  if(ticks==500){if(!family.isRemoved())throw new IllegalStateException("Client garage store failed");System.out.println("NG_DEV9_CLIENT_STORE_PASS");}
  if(ticks==550){if(l.getEntity(family.getUUID())==null)throw new IllegalStateException("Client garage recall failed");System.out.println("NG_DEV9_CLIENT_DEALER_GARAGE_PASS");p.closeContainer();p.teleportTo(0,140,2);VehicleManagement.open(city,p);}
  if(ticks==630){if(!city.systems.comfort.plate().equals("NG DEV 9"))throw new IllegalStateException("Client plate edit failed");System.out.println("NG_DEV9_CLIENT_PLATE_PASS");}
 }
 private static NgCityEntity spawn(net.minecraft.server.level.ServerLevel l,net.minecraft.server.level.ServerPlayer p,VehicleSpec s,double x,double z){var c=VehicleContent.type(s).create(l);c.setPos(x,140,z);l.addFreshEntity(c);c.equipment.claim(p);c.systems.comfort.setPlate("NG "+(int)x+" 9");return c;}
}
