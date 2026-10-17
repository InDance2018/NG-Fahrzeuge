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
public final class Dev12Scene {
 static int ticks;static NgCityEntity offroad,sport,taxi,tractor;static TrailerEntity semi;static BlockPos bench=new BlockPos(6,140,7);static boolean remote;
 @SubscribeEvent public static void tick(TickEvent.ServerTickEvent e){
  if(!Boolean.getBoolean("ng_fahrzeuge.dev12Visual")||e.phase!=TickEvent.Phase.END||e.getServer().getPlayerList().getPlayers().isEmpty())return;ticks++;var p=e.getServer().getPlayerList().getPlayers().get(0);var l=p.serverLevel();
  if(ticks==1)for(var rule:java.util.List.of(GameRules.RULE_DOMOBLOOT,GameRules.RULE_DOMOBSPAWNING,GameRules.RULE_DOFIRETICK))l.getGameRules().getRule(rule).set(false,e.getServer());
  if(ticks==40){p.stopRiding();p.setCamera(p);p.setGameMode(GameType.CREATIVE);for(var old:l.getEntitiesOfClass(net.minecraft.world.entity.Entity.class,new net.minecraft.world.phys.AABB(-30,130,-30,135,170,125),x->x instanceof NgCityEntity||x instanceof TrailerEntity))old.discard();
   for(int x=-12;x<120;x++)for(int z=-15;z<110;z++){l.setBlockAndUpdate(new BlockPos(x,139,z),Blocks.SMOOTH_STONE.defaultBlockState());if(z<45&&x<90)for(int y=140;y<144;y++)l.setBlockAndUpdate(new BlockPos(x,y,z),Blocks.AIR.defaultBlockState());}
   l.setDayTime(5000);l.setWeatherParameters(100000,0,false,false);
   offroad=spawn(l,p,VehicleSpec.OFFROAD,0,0);p.teleportTo(0,140,1);offroad.systems.mechanics.action(p,24);offroad.systems.mechanics.action(p,25);offroad.systems.mechanics.toggleDoor(p,0);offroad.systems.mechanics.toggleDoor(p,3);
   sport=spawn(l,p,VehicleSpec.SPORT,12,0);taxi=spawn(l,p,VehicleSpec.TAXI,24,0);tractor=spawn(l,p,VehicleSpec.TRACTOR,48,0);semi=VehicleContent.TRAILER.get().create(l);semi.variant(3);semi.setPos(48,140,-4.15);semi.claim(p);semi.cargo.setItem(107,new ItemStack(Items.DIAMOND,9));l.addFreshEntity(semi);l.setBlockAndUpdate(bench,VehicleContent.WORKBENCH.get().defaultBlockState());view(p,6,142.5,7,140,14);System.out.println("NG_DEV12_SCENE_READY");}
  if(ticks==150)view(p,18,142,6,135,14);
  if(ticks==250)view(p,30,142.5,6,135,14);
  if(ticks==330){for(int i=0;i<2;i++){var npc=new net.minecraft.world.entity.player.Player(l,BlockPos.ZERO,0,new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"TaxiFixture"+i)){@Override public boolean isSpectator(){return false;}@Override public boolean isCreative(){return false;}};npc.startRiding(taxi);}taxi.systems.sync();}
  if(ticks==380)view(p,56,144,8,145,17);
  if(ticks==470){p.setGameMode(GameType.CREATIVE);p.teleportTo(48,140,1);if(!tractor.systems.couple(p)||!semi.toggleSupports(p))throw new IllegalStateException("Client semi fixture did not couple");System.out.println("NG_DEV12_COUPLED_PASS");view(p,53,139.5,-1.05,90,6);}
  if(ticks==560){p.setGameMode(GameType.CREATIVE);p.setCamera(p);p.teleportTo(0,140,1);offroad.systems.closeDoors();offroad.systems.damage(50);var state=new net.minecraft.nbt.CompoundTag();offroad.systems.save(state);state.putFloat("Coolant",1.5F);state.putFloat("Temperature",48);offroad.systems.load(state);if(!VehicleDiagnosisMenu.open(offroad,p))throw new IllegalStateException("Diagnosis fixture failed");}
  if(ticks==640){p.closeContainer();VehicleManagement.open(offroad,p);}
  if(ticks==720){p.closeContainer();p.teleportTo(6,140,8);var state=l.getBlockState(bench);state.use(l,p,net.minecraft.world.InteractionHand.MAIN_HAND,new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(bench),net.minecraft.core.Direction.UP,bench,false));}
  if(ticks==790){if(p.getInventory().countItem(VehicleContent.TRACTOR_ITEM.get())<1)throw new IllegalStateException("Client workbench did not build tractor");System.out.println("NG_DEV12_CLIENT_ASSEMBLY_PASS");p.closeContainer();p.teleportTo(semi.getX()+2,140,semi.getZ());TrailerCargoMenu.open(semi,p,0);}
  if(ticks==860){p.closeContainer();p.teleportTo(0,140,1);var key=new ItemStack(VehicleContent.KEY.get());offroad.equipment.bindKey(p,key);p.getInventory().setItem(0,key);p.getInventory().selected=0;p.inventoryMenu.broadcastChanges();p.teleportTo(10,140,0);}
  if(ticks==940){if(!offroad.isLocked())throw new IllegalStateException("Client remote key failed");System.out.println("NG_DEV12_CLIENT_REMOTE_PASS");p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,ItemStack.EMPTY);p.teleportTo(12,140,1);p.startRiding(sport);}
  if(ticks==1080){if(!sport.systems.mechanics.manual()||sport.systems.mechanics.gear()<3||sport.systems.metres()<2)throw new IllegalStateException("Client manual gear/drive failed");System.out.println("NG_DEV12_CLIENT_MANUAL_DRIVE_PASS");p.stopRiding();p.teleportTo(0,140,1);offroad.toggleLock(p);p.startRiding(offroad);}
  if(ticks==1210){if(!offroad.engineRunning()||offroad.systems.metres()<1||!offroad.systems.mechanics.awd()||!offroad.systems.mechanics.low()||Math.abs(offroad.getDriveSpeed())>.190)throw new IllegalStateException("Client low range drive failed");System.out.println("NG_DEV12_CLIENT_LOW_RANGE_PASS");p.stopRiding();}
 }
 static void view(net.minecraft.server.level.ServerPlayer p,double x,double y,double z,float yaw,float pitch){p.stopRiding();p.setCamera(p);p.setGameMode(GameType.SPECTATOR);p.teleportTo(p.serverLevel(),x,y,z,yaw,pitch);}
 static NgCityEntity spawn(net.minecraft.server.level.ServerLevel l,net.minecraft.server.level.ServerPlayer p,VehicleSpec s,double x,double z){var c=VehicleContent.type(s).create(l);c.setPos(x,140,z);l.addFreshEntity(c);c.equipment.claim(p);c.systems.comfort.setPlate("NG DEV 12");return c;}
}
