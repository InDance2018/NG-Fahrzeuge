package de.nevergate.fahrzeuge;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(value=NgFahrzeuge.MOD_ID,namespace=NgFahrzeuge.MOD_ID)
public final class Dev9GameTests {
 private static NgCityEntity car(GameTestHelper h,VehicleSpec spec){var c=VehicleContent.type(spec).create(h.getLevel());var pos=h.absolutePos(new BlockPos(1,1,1));c.setPos(pos.getX()+.5,h.getLevel().getMaxBuildHeight()-12,pos.getZ()+.5);c.setNoGravity(true);h.getLevel().addFreshEntity(c);return c;}
 private static Player owner(GameTestHelper h,NgCityEntity c){var p=h.makeMockPlayer();p.setUUID(UUID.randomUUID());p.getAbilities().mayBuild=true;p.getAbilities().instabuild=false;p.setPos(c.position());c.equipment.claim(p);return p;}
 private static void battery(NgCityEntity c,float value){var t=new CompoundTag();c.systems.save(t);t.putFloat("Battery",value);c.systems.load(t);}
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void batteryComfortAndPlateSurvivePacking(GameTestHelper h){
  var c=car(h,VehicleSpec.FAMILY);var p=owner(h,c);battery(c,42);
  h.assertTrue(c.systems.comfort.action(p,VehicleNetwork.WIPER_ACTION)&&c.systems.comfort.action(p,VehicleNetwork.WINDOW_ACTION)&&c.systems.comfort.action(p,VehicleNetwork.CABIN_ACTION),"Comfort actions rejected");
  h.assertTrue(c.systems.comfort.setPlate("NG IN 209")&&!c.systems.comfort.setPlate("§bad"),"Plate validation failed");c.systems.comfort.setVariant(1);
  var loaded=VehicleContent.NG_FAMILY.get().create(h.getLevel());loaded.loadEquipment(c.getPickResult().getTag());
  h.assertTrue(loaded.systems.comfort.wipers()&&loaded.systems.comfort.windows()&&loaded.systems.comfort.cabin()&&loaded.systems.comfort.variant()==1&&loaded.systems.comfort.plate().equals("NG IN 209")&&Math.abs(loaded.systems.comfort.battery()-41.9)<.01,"Packed comfort state lost");c.discard();h.succeed();
 }
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void emptyBatteryRejectsStartAndAccessoriesDrainCharge(GameTestHelper h){
  var c=car(h,VehicleSpec.CITY);var p=owner(h,c);battery(c,0);p.startRiding(c);
  h.assertFalse(c.applyAction(p,VehicleNetwork.ENGINE_ACTION),"Flat battery started engine");h.assertFalse(c.systems.comfort.action(p,VehicleNetwork.WINDOW_ACTION),"Flat battery operated window");
  battery(c,10);c.systems.comfort.action(p,VehicleNetwork.CABIN_ACTION);float before=c.systems.comfort.battery();for(int i=0;i<20;i++)c.systems.comfort.tick();h.assertTrue(c.systems.comfort.battery()<before,"Cabin light did not drain battery");
  h.assertTrue(c.applyAction(p,VehicleNetwork.ENGINE_ACTION),"Charged car did not start");before=c.systems.comfort.battery();for(int i=0;i<20;i++)c.systems.comfort.tick();h.assertTrue(c.systems.comfort.battery()>before,"Alternator did not charge");c.discard();h.succeed();
 }
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void jumperConservesChargeAndRejectsStranger(GameTestHelper h){
  var a=car(h,VehicleSpec.CITY);var p=owner(h,a);var b=car(h,VehicleSpec.CITY);b.setPos(a.position().add(4,0,0));b.equipment.claim(p);battery(a,50);battery(b,0);
  h.assertTrue(b.systems.comfort.jumpFrom(a,p)&&a.systems.comfort.battery()==40&&b.systems.comfort.battery()==10,"Jump did not conserve charge");var other=h.makeMockPlayer();other.setUUID(UUID.randomUUID());other.setPos(b.position());h.assertFalse(b.systems.comfort.jumpFrom(a,other),"Stranger used batteries");a.discard();b.discard();h.succeed();
 }
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void dealerChecksWholePriceBeforeConsuming(GameTestHelper h){
  var c=car(h,VehicleSpec.CITY);var p=owner(h,c);int price=VehicleDealerConfig.price(VehicleSpec.CITY);var payment=VehicleDealerConfig.payment();p.getInventory().setItem(5,new ItemStack(payment,price-1));
  h.assertFalse(VehicleDealerConfig.buy(p,VehicleSpec.CITY),"Underpaid purchase succeeded");h.assertTrue(p.getInventory().countItem(payment)==price-1,"Failed purchase consumed payment");p.getInventory().setItem(5,new ItemStack(payment,price));h.assertTrue(VehicleDealerConfig.buy(p,VehicleSpec.CITY)&&p.getInventory().countItem(payment)==0&&p.getInventory().countItem(VehicleContent.NG_CITY_ITEM.get())==1,"Purchase not atomic");c.discard();h.succeed();
 }
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void garageRestoresUuidCargoAndPreventsDoubleRecall(GameTestHelper h){
  var c=car(h,VehicleSpec.FAMILY);var p=owner(h,c);c.equipment.cargo.setItem(0,new ItemStack(Items.DIAMOND,7));c.systems.comfort.setPlate("NG GAR 9");var id=c.getUUID();BlockPos pos=c.blockPosition().offset(0,0,6);h.getLevel().setBlockAndUpdate(pos,VehicleContent.GARAGE.get().defaultBlockState());
  var data=VehicleGarageData.get(h.getLevel());h.assertTrue(data.store(c,p)&&c.isRemoved(),"Garage store failed");var restored=VehicleGarageData.load(data.save(new CompoundTag()));
  p.setPos(pos.getX()+.5,pos.getY(),pos.getZ()+.5);h.assertTrue(restored.recall(p,0,pos),"Garage recall failed");var actual=(NgCityEntity)h.getLevel().getEntity(id);h.assertTrue(actual!=null&&actual.equipment.cargo.getItem(0).getCount()==7&&actual.systems.comfort.plate().equals("NG GAR 9"),"Garage lost identity/cargo");h.assertFalse(restored.recall(p,0,pos),"Second recall duplicated car");actual.discard();data.entries(p.getUUID()).clear();h.getLevel().removeBlock(pos,false);h.succeed();
 }
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void garageBlockedExitRetainsRecord(GameTestHelper h){
  var c=car(h,VehicleSpec.CITY);var p=owner(h,c);BlockPos pos=c.blockPosition().offset(0,0,6);h.getLevel().setBlockAndUpdate(pos,VehicleContent.GARAGE.get().defaultBlockState());var data=new VehicleGarageData();h.assertTrue(data.store(c,p),"Store failed");p.setPos(pos.getX(),pos.getY(),pos.getZ());
  var obstruction=car(h,VehicleSpec.CITY);obstruction.setPos(pos.getX()+.5,pos.getY()+.25,pos.getZ()+.5-3-VehicleSpec.CITY.length/2);
  h.assertFalse(data.recall(p,0,pos),"Blocked exit recalled vehicle");h.assertTrue(data.entries(p.getUUID()).size()==1,"Blocked recall destroyed record");obstruction.discard();h.getLevel().removeBlock(pos,false);h.succeed();
 }
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void ropeFollowsAndConnectedCarCannotBeStoredOrTransferred(GameTestHelper h){
  var a=car(h,VehicleSpec.FAMILY);var p=owner(h,a);var b=car(h,VehicleSpec.CITY);b.setPos(a.position().add(0,0,-6));b.equipment.claim(p);
  h.assertTrue(VehicleRecovery.connect(a,b,p,1),"Rope connect failed");double old=b.getZ();a.setPos(a.position().add(0,0,1));b.tick();h.assertTrue(b.getZ()>old,"Towed vehicle did not follow");
  p.setPos(b.position());h.assertFalse(new VehicleGarageData().store(b,p),"Connected vehicle stored");h.assertFalse(b.equipment.changeOwner(p,UUID.randomUUID()),"Connected ownership changed");p.setPos(a.position());h.assertTrue(VehicleRecovery.detach(a,p)&&!b.systems.comfort.connected(),"Rope detach failed");a.discard();b.discard();h.succeed();
 }
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void flatbedCarriesVehicleAndPersistsConnection(GameTestHelper h){
  var a=car(h,VehicleSpec.TOW_TRUCK);var p=owner(h,a);var b=car(h,VehicleSpec.CITY);b.setPos(a.position().add(0,0,-6));b.equipment.claim(p);h.assertTrue(VehicleRecovery.connect(a,b,p,2),"Loading failed");b.tick();h.assertTrue(Math.abs(b.getY()-a.getY()-1.1)<.001,"Vehicle not on flatbed");
  var tag=new CompoundTag();b.saveWithoutId(tag);var copy=VehicleContent.NG_CITY.get().create(h.getLevel());copy.load(tag);h.assertTrue(a.getUUID().equals(copy.systems.comfort.parent())&&copy.systems.comfort.mode()==2&&!copy.systems.canRun(),"Recovery persistence/start lock failed");p.setPos(a.position());h.assertTrue(VehicleRecovery.detach(a,p),"Unloading failed");a.discard();b.discard();h.succeed();
 }
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void liftRaisesRepairsAndLowersOwnedCar(GameTestHelper h){
  var c=car(h,VehicleSpec.CITY);var p=owner(h,c);BlockPos pos=c.blockPosition().offset(2,0,0);h.getLevel().setBlockAndUpdate(pos,VehicleContent.LIFT.get().defaultBlockState());var lift=(VehicleLiftEntity)h.getLevel().getBlockEntity(pos);double y=c.getY();
  h.assertTrue(lift.use(p)&&Math.abs(c.getY()-pos.getY()-1.75)<.001&&c.systems.comfort.restrained(),"Lift failed to raise/hold");c.systems.damage(30);p.getInventory().setItem(0,new ItemStack(VehicleContent.REPAIR_TOOL.get()));p.getInventory().setItem(8,new ItemStack(VehicleContent.PARTS.get()));h.assertTrue(lift.use(p)&&c.systems.body()==95,"Lift repair failed");p.getInventory().setItem(0,ItemStack.EMPTY);
  h.assertTrue(lift.use(p)&&Math.abs(c.getY()-pos.getY()-.25)<.001&&!c.systems.comfort.connected(),"Lift did not lower/release");c.discard();h.getLevel().removeBlock(pos,false);h.succeed();
 }
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void variantItemsPlaceAndRepackWithoutLosingVariant(GameTestHelper h){
  var fixture=car(h,VehicleSpec.CITY);var p=owner(h,fixture);var pos=fixture.blockPosition().below();fixture.discard();h.getLevel().setBlockAndUpdate(pos,net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
  for(var item:new net.minecraft.world.item.Item[]{VehicleContent.FAMILY_ROOF.get(),VehicleContent.VAN_WINDOWS.get()}){
   var stack=new ItemStack(item);p.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,stack);
   var context=new net.minecraft.world.item.context.UseOnContext(h.getLevel(),p,net.minecraft.world.InteractionHand.MAIN_HAND,stack,new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atBottomCenterOf(pos.above()),net.minecraft.core.Direction.UP,pos,false));
   h.assertTrue(item.useOn(context).consumesAction()&&stack.isEmpty(),"Variant item placement failed");
   var list=h.getLevel().getEntitiesOfClass(NgCityEntity.class,new net.minecraft.world.phys.AABB(pos).inflate(4));h.assertTrue(list.size()==1&&list.get(0).systems.comfort.variant()==1,"Variant not applied");
   var c=list.get(0);var packed=c.getPickResult();var restored=VehicleContent.type(c.spec()).create(h.getLevel());restored.loadEquipment(packed.getTag());h.assertTrue(restored.systems.comfort.variant()==1,"Packing lost variant");c.discard();
  }h.getLevel().removeBlock(pos,false);h.succeed();
 }
 @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
 public static void garageDiskReloadPreservesStoredVehicle(GameTestHelper h) throws Exception {
  var c=car(h,VehicleSpec.CITY);var p=owner(h,c);c.equipment.cargo.setItem(0,new ItemStack(Items.DIAMOND,4));var data=new VehicleGarageData();h.assertTrue(data.store(c,p),"Store fixture failed");
  var dir=java.nio.file.Files.createTempDirectory("ng-garage-disk-");
  try{
   var write=new net.minecraft.world.level.storage.DimensionDataStorage(dir.toFile(),net.minecraft.util.datafix.DataFixers.getDataFixer());write.set("ng_fahrzeuge_garage",data);write.save();
   var read=new net.minecraft.world.level.storage.DimensionDataStorage(dir.toFile(),net.minecraft.util.datafix.DataFixers.getDataFixer());var loaded=read.computeIfAbsent(VehicleGarageData.FACTORY,"ng_fahrzeuge_garage");
   h.assertTrue(loaded.entries(p.getUUID()).size()==1,"Real disk reload lost garage record");var tag=loaded.entries(p.getUUID()).get(0);var restored=VehicleContent.NG_CITY.get().create(h.getLevel());restored.load(tag);
   h.assertTrue(restored.equipment.cargo.getItem(0).getCount()==4&&restored.getUUID().equals(c.getUUID()),"Disk reload changed cargo/identity");
  }finally{try(var paths=java.nio.file.Files.walk(dir)){for(var path:paths.sorted(java.util.Comparator.reverseOrder()).toList())java.nio.file.Files.delete(path);}}
  h.succeed();
 }
}
