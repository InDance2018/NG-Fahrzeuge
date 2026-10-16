package de.nevergate.fahrzeuge;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.gametest.GameTestHolder;

/** Test fixtures stay outside the structure clearing area; no production save hooks are added. */
@GameTestHolder(value = NgFahrzeuge.MOD_ID, namespace = NgFahrzeuge.MOD_ID)
public final class PersistenceGameTests {
    private static CompoundTag equipment(int color) {
        CompoundTag tag=new CompoundTag();tag.putInt("VehicleColor",color);
        tag.putBoolean("ParkingBrake",true);tag.putBoolean("VehicleLights",true);return tag;
    }

    @GameTest(template="forge:empty3x3x3", timeoutTicks=160)
    public static void realServerRestartCheckpoint(GameTestHelper h) throws Exception {
        String phase=System.getProperty("ng_fahrzeuge.persistencePhase","none");
        Path manifest=Path.of("ng-dev4-restart-checkpoint.txt");
        if(phase.equals("write")) {
            for(VehicleSpec spec:VehicleSpec.values()) {
                BlockPos pos=new BlockPos(128+spec.ordinal()*8,160,128);
                h.getLevel().getChunkAt(pos);
                h.getLevel().getChunkSource().addRegionTicket(TicketType.PORTAL,new ChunkPos(pos),3,pos);
            }
            h.runAfterDelay(25,()->{
                try {
                    var lines=new ArrayList<String>();
                    for(VehicleSpec spec:VehicleSpec.values()) {
                        BlockPos pos=new BlockPos(128+spec.ordinal()*8,160,128);
                        h.assertTrue(h.getLevel().areEntitiesLoaded(new ChunkPos(pos).toLong()),"Checkpoint entity storage not loaded");
                        NgCityEntity car=VehicleContent.type(spec).create(h.getLevel());
                        car.setPos(pos.getX()+0.5,pos.getY(),pos.getZ()+0.5);car.setNoGravity(true);
                        car.setCustomName(Component.literal("NG-Dev4-Restart-"+spec.name()));
                        car.loadEquipment(equipment((2+spec.ordinal()*3)%16));
                        var owner=h.makeMockPlayer();owner.setUUID(UUID.fromString("00000000-0000-0000-0000-000000000011"));owner.setPos(car.position());owner.getAbilities().mayBuild=true;car.equipment.claim(owner);
                        h.assertTrue(car.systems.expansion.setSeat(owner,2,3),"Checkpoint seat configuration rejected");
                        if(spec==VehicleSpec.BUS)h.assertTrue(car.systems.expansion.setRoute(owner,"R11","Neustart"),"Checkpoint route rejected");
                        var state=new CompoundTag();car.systems.save(state);state.putBoolean("Fog",true);car.systems.load(state);VehicleDirectoryData.update(car);

                        h.assertTrue(h.getLevel().addFreshEntity(car),"Checkpoint vehicle was not added");
                        lines.add(spec.name()+","+car.getUUID());
                    }
                    Files.write(manifest,lines);
                    h.runAfterDelay(5,h::succeed);
                } catch(java.io.IOException ex) { throw new RuntimeException(ex); }
            });
        } else if(phase.equals("read")) {
            var lines=Files.readAllLines(manifest);h.assertTrue(lines.size()==VehicleSpec.values().length,"Restart manifest missing vehicles");
            for(VehicleSpec spec:VehicleSpec.values()) {
                BlockPos pos=new BlockPos(128+spec.ordinal()*8,160,128);
                h.getLevel().getChunkAt(pos);
                h.getLevel().getChunkSource().addRegionTicket(TicketType.PORTAL,new ChunkPos(pos),3,pos);
            }
            h.runAfterDelay(25,()->{
                for(String line:lines) {
                    String[] parts=line.split(",");VehicleSpec spec=VehicleSpec.valueOf(parts[0]);
                    var entity=h.getLevel().getEntity(UUID.fromString(parts[1]));
                    h.assertTrue(entity instanceof NgCityEntity,"Vehicle missing after actual server restart: "+spec);
                    NgCityEntity car=(NgCityEntity)entity;
                    h.assertTrue(car.spec()==spec&&car.color()==(2+spec.ordinal()*3)%16&&car.lightsOn()&&car.isParkingBraked(),"Restart lost type or equipment: "+spec);
                    h.assertTrue(car.getDriveSpeed()==0&&car.getDriver()==null,"Restart restored motion or phantom driver");
                    h.assertTrue(Math.abs(car.getX()-(128+spec.ordinal()*8+0.5))<0.001&&car.getY()==160,"Restart lost position");
                    h.assertTrue(car.getCustomName().getString().equals("NG-Dev4-Restart-"+spec.name()),"Restart lost name");
                    var checkpointOwner=UUID.fromString("00000000-0000-0000-0000-000000000011");
                    h.assertTrue(restoredDirectoryHas(h,checkpointOwner,car.getUUID()),"Directory record missing after real process restart");
                    h.assertTrue(car.systems.expansion.seatForward()==2&&car.systems.expansion.seatHeight()==3&&car.systems.expansion.fog(),"Dev11 seat/lamp state lost on real restart");
                    if(spec==VehicleSpec.BUS)h.assertTrue(car.systems.expansion.route().equals("R11")&&car.systems.expansion.destination().equals("Neustart"),"Bus route lost on real restart");
                    car.discard();
                }
                h.succeed();
            });
        } else h.succeed();
    }

    private static boolean restoredDirectoryHas(GameTestHelper h,UUID owner,UUID id){return VehicleDirectoryData.get(h.getLevel()).own(owner).stream().anyMatch(t->t.getUUID("Id").equals(id));}

    @GameTest(template="forge:empty3x3x3", timeoutTicks=900)
    public static void actualChunkUnloadAndReloadPreserveVehicle(GameTestHelper h) {
        BlockPos pos=new BlockPos(512,160,512);h.getLevel().getChunkAt(pos);
        h.getLevel().setChunkForced(32,32,true);
        waitForFixture(h,pos,0);
    }

    private static void waitForFixture(GameTestHelper h,BlockPos pos,int attempt) {
        h.runAfterDelay(20,()->{
            if(!h.getLevel().areEntitiesLoaded(new ChunkPos(pos).toLong())
                    ||!h.getLevel().isPositionEntityTicking(pos)) {
                if(attempt>=12)h.getLevel().setChunkForced(32,32,false);
                h.assertTrue(attempt<12,"Forced chunk fixture did not become entity-ready");
                waitForFixture(h,pos,attempt+1);return;
            }
            NgCityEntity car=VehicleContent.NG_FAMILY.get().create(h.getLevel());
            car.setPos(512.5,160,512.5);car.setNoGravity(true);car.loadEquipment(equipment(14));
            car.setCustomName(Component.literal("NG-Dev4-Chunk"));
            h.assertTrue(h.getLevel().addFreshEntity(car),"Chunk fixture vehicle was not added");
            h.runAfterDelay(5,()->{
                h.getLevel().setChunkForced(32,32,false);
                pollUnload(h,new ChunkPos(pos),car,0);
            });
        });
    }

    private static void pollUnload(GameTestHelper h,ChunkPos chunk,NgCityEntity original,int attempt) {
        h.runAfterDelay(20,()->{
            if(!h.getLevel().getChunkSource().hasChunk(chunk.x,chunk.z)
                    &&h.getLevel().getEntity(original.getUUID())==null) {
                h.getLevel().getChunk(chunk.x,chunk.z);
                BlockPos ticketPos=new BlockPos(chunk.getMinBlockX(),160,chunk.getMinBlockZ());
                h.getLevel().setChunkForced(chunk.x,chunk.z,true);
                pollReload(h,chunk,original,0);
            } else {
                h.assertTrue(attempt<38,"Chunk did not actually unload within test window");
                pollUnload(h,chunk,original,attempt+1);
            }
        });
    }
    private static void pollReload(GameTestHelper h,ChunkPos chunk,NgCityEntity original,int attempt) {
        h.runAfterDelay(20,()->{
            var entity=h.getLevel().getEntity(original.getUUID());
            if(entity==null) {
                if(attempt>=12)h.getLevel().setChunkForced(chunk.x,chunk.z,false);
                h.assertTrue(attempt<12,"Entity not restored after chunk became loaded; entitiesReady="
                        +h.getLevel().areEntitiesLoaded(chunk.toLong()));
                pollReload(h,chunk,original,attempt+1);return;
            }
            h.assertTrue(entity instanceof NgCityEntity&&entity!=original,"Chunk did not restore a new entity instance");
            NgCityEntity restored=(NgCityEntity)entity;
            h.assertTrue(restored.spec()==VehicleSpec.FAMILY&&restored.color()==14&&restored.lightsOn()
                    &&restored.isParkingBraked()&&restored.getDriveSpeed()==0,"Chunk reload lost vehicle state");
            h.assertTrue(restored.getCustomName().getString().equals("NG-Dev4-Chunk"),"Chunk reload lost name");
            restored.discard();h.getLevel().setChunkForced(chunk.x,chunk.z,false);h.succeed();
        });
    }

}
