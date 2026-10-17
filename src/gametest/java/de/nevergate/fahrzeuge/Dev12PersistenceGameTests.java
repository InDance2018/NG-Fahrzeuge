package de.nevergate.fahrzeuge;
import java.nio.file.*;import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.item.*;
import net.minecraftforge.gametest.GameTestHolder;
@GameTestHolder(value=NgFahrzeuge.MOD_ID,namespace=NgFahrzeuge.MOD_ID)
public final class Dev12PersistenceGameTests {
 @GameTest(template="forge:empty3x3x3",timeoutTicks=200)
 public static void actualServerRestartPreservesFifthWheelAndLastSemiSlot(GameTestHelper h)throws Exception{
  String phase=System.getProperty("ng_fahrzeuge.persistencePhase","none");if(!phase.equals("write")&&!phase.equals("read")){h.succeed();return;}var l=h.getLevel();var file=Path.of("ng-dev12-semi-restart.txt");
  for(var p:List.of(new BlockPos(256,160,128),new BlockPos(256,160,124))){l.getChunkAt(p);l.getChunkSource().addRegionTicket(TicketType.PORTAL,new ChunkPos(p),3,p);}
  if(phase.equals("write"))h.runAfterDelay(25,()->{try{var c=VehicleContent.NG_TRACTOR.get().create(l);c.setPos(256.5,160,128.5);c.setNoGravity(true);h.assertTrue(l.addFreshEntity(c),"Tractor restart fixture failed");var p=h.makeMockPlayer();p.setUUID(UUID.randomUUID());p.setPos(c.position());p.getAbilities().mayBuild=true;c.equipment.claim(p);var t=VehicleContent.TRAILER.get().create(l);t.variant(3);t.setPos(256.5,160,124.35);t.setNoGravity(true);t.claim(p);t.cargo.setItem(107,new ItemStack(Items.DIAMOND,9));h.assertTrue(l.addFreshEntity(t)&&c.systems.couple(p)&&t.toggleSupports(p),"Fifth-wheel restart fixture failed");Files.write(file,List.of(c.getUUID().toString(),t.getUUID().toString()));h.runAfterDelay(5,h::succeed);}catch(java.io.IOException ex){throw new RuntimeException(ex);}});
  else{var ids=Files.readAllLines(file);h.runAfterDelay(25,()->{var c=l.getEntity(UUID.fromString(ids.get(0)));var t=l.getEntity(UUID.fromString(ids.get(1)));h.assertTrue(c instanceof NgCityEntity&&t instanceof TrailerEntity,"Actual restart lost tractor/trailer");var car=(NgCityEntity)c;var trailer=(TrailerEntity)t;h.assertTrue(car.spec()==VehicleSpec.TRACTOR&&trailer.variant()==3&&car.systems.trailerId().equals(trailer.getUUID())&&trailer.towId().equals(car.getUUID()),"Actual restart lost reciprocal fifth-wheel UUIDs");h.assertTrue(!trailer.supports()&&trailer.cargo.getContainerSize()==108&&trailer.cargo.getItem(107).getCount()==9&&car.systems.canRun(),"Actual restart lost supports/cargo or permanently blocked tractor");car.discard();trailer.discard();h.succeed();});}
 }
}
