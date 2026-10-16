package de.nevergate.fahrzeuge.visualtest;
import de.nevergate.fahrzeuge.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber(modid=NgFahrzeuge.MOD_ID)
public final class Dev10Scene {
 static int ticks;static NgCityEntity city,truck,load,family,sale;static BlockPos dealer=new BlockPos(15,140,7);
 @SubscribeEvent public static void tick(TickEvent.ServerTickEvent e){
  if(!Boolean.getBoolean("ng_fahrzeuge.dev10Visual")||e.phase!=TickEvent.Phase.END||e.getServer().getPlayerList().getPlayers().isEmpty())return;
  ticks++;var p=e.getServer().getPlayerList().getPlayers().get(0);var l=p.serverLevel();
  if(ticks==1)for(var rule:java.util.List.of(GameRules.RULE_DOMOBLOOT,GameRules.RULE_DOMOBSPAWNING,GameRules.RULE_DOFIRETICK))l.getGameRules().getRule(rule).set(false,e.getServer());
  if(ticks==40){
   p.stopRiding();p.setCamera(p);p.setGameMode(GameType.CREATIVE);for(var old:l.getEntitiesOfClass(net.minecraft.world.entity.Entity.class,new net.minecraft.world.phys.AABB(-30,130,-30,85,170,120),x->x instanceof NgCityEntity||x instanceof TrailerEntity))old.discard();
   l.setDayTime(5000);l.setWeatherParameters(100000,0,false,false);for(int x=-10;x<75;x++)for(int z=-15;z<100;z++)l.setBlockAndUpdate(new BlockPos(x,139,z),Blocks.SMOOTH_STONE.defaultBlockState());
   city=spawn(l,p,VehicleSpec.CITY,0,0);p.teleportTo(0,140,2);city.systems.upgrades.tool(p,new ItemStack(VehicleContent.RACK.get()));city.systems.upgrades.roof.setItem(17,new ItemStack(Items.DIAMOND,4));city.systems.upgrades.action(p,VehicleNetwork.HOOD_ACTION);var state=new net.minecraft.nbt.CompoundTag();city.systems.save(state);state.putFloat("Dirt",80);state.putFloat("Tyre0",7);state.putFloat("Tyre1",7);city.systems.load(state);
   family=spawn(l,p,VehicleSpec.FAMILY,12,0);p.teleportTo(12,140,2);family.systems.action(p,VehicleNetwork.HATCH_ACTION);
   truck=spawn(l,p,VehicleSpec.TOW_TRUCK,38,0);p.teleportTo(38,140,2);truck.systems.upgrades.action(p,VehicleNetwork.BEACON_ACTION);truck.systems.comfort.toggleRamp();load=spawn(l,p,VehicleSpec.CITY,38,-6);
   for(int i=1;i<=2;i++){var t=VehicleContent.TRAILER.get().create(l);t.variant(i);t.setPos(50+(i-1)*6,140,0);t.claim(p);l.addFreshEntity(t);}
   l.setBlockAndUpdate(dealer,VehicleContent.DEALER.get().defaultBlockState());l.setBlockAndUpdate(new BlockPos(4,140,7),VehicleContent.WASH.get().defaultBlockState());
   p.setGameMode(GameType.SPECTATOR);p.teleportTo(l,4,142.3,6,145,18);System.out.println("NG_DEV10_SCENE_READY");
  }
  if(ticks==170){p.setGameMode(GameType.CREATIVE);p.teleportTo(38,140,-2);if(!VehicleRecovery.connect(truck,load,p,4))throw new IllegalStateException("Winch fixture failed");p.setGameMode(GameType.SPECTATOR);p.teleportTo(l,44,143,-10,35,15);}
  if(ticks==420){p.setGameMode(GameType.CREATIVE);p.teleportTo(15,140,8);sale=spawn(l,p,VehicleSpec.CITY,18,5);VehicleServiceMenu.open(p,dealer,0);}
  if(ticks==490){if(!sale.isRemoved())throw new IllegalStateException("Client sale failed");System.out.println("NG_DEV10_CLIENT_SALE_PASS");p.closeContainer();p.teleportTo(0,140,2);city.systems.upgrades.action(p,VehicleNetwork.ROOF_ACTION);}
  if(ticks==550){p.closeContainer();city.systems.upgrades.action(p,VehicleNetwork.HOOD_ACTION);p.startRiding(family);}
  if(ticks==660){if(!family.engineRunning()||family.systems.metres()<=0)throw new IllegalStateException("Client drive failed");System.out.println("NG_DEV10_CLIENT_CAMERA_DRIVE_PASS");}
  if(ticks==680)p.stopRiding();
 }
 private static NgCityEntity spawn(net.minecraft.server.level.ServerLevel l,net.minecraft.server.level.ServerPlayer p,VehicleSpec s,double x,double z){var c=VehicleContent.type(s).create(l);c.setPos(x,140,z);l.addFreshEntity(c);c.equipment.claim(p);c.systems.comfort.setPlate("NG DEV 10");return c;}
}
