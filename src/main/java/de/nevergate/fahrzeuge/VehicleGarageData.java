package de.nevergate.fahrzeuge;

import java.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Nine server-owned vehicle records per account, shared by its garage terminals. */
public final class VehicleGarageData extends SavedData {
    private final Map<UUID,List<CompoundTag>> records=new HashMap<>();
    public static final SavedData.Factory<VehicleGarageData> FACTORY=new SavedData.Factory<>(VehicleGarageData::new,VehicleGarageData::load,net.minecraft.util.datafix.DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
    public static VehicleGarageData get(ServerLevel level){return level.getServer().overworld().getDataStorage().computeIfAbsent(
            FACTORY,"ng_fahrzeuge_garage");}
    public List<CompoundTag> entries(UUID owner){return records.computeIfAbsent(owner,k->new ArrayList<>());}
    public boolean store(NgCityEntity car,net.minecraft.world.entity.player.Player p){
        if(car.level().isClientSide||!car.systems.serviceAllowed(p)||!car.equipment.isOwner(p)||car.isVehicle()
                ||car.systems.attached()||car.systems.comfort.connected()||car.equipment.hasOpeners()||entries(p.getUUID()).size()>=9)return false;
        CompoundTag tag=new CompoundTag();car.saveWithoutId(tag);tag.putString("GarageSpec",car.spec().id);
        entries(p.getUUID()).add(tag);car.discard();VehicleDirectoryData.stored(car);setDirty();return true;
    }
    public boolean recall(net.minecraft.world.entity.player.Player p,int index,net.minecraft.core.BlockPos terminal){
        var list=entries(p.getUUID());if(!(p.level() instanceof ServerLevel level)||!p.isAlive()||p.isSpectator()||!p.getAbilities().mayBuild||p.distanceToSqr(terminal.getX()+.5,terminal.getY()+.5,terminal.getZ()+.5)>64||!level.getBlockState(terminal).is(VehicleContent.GARAGE.get())||!level.mayInteract(p,terminal)||index<0||index>=list.size())return false;
        var tag=list.get(index);VehicleSpec spec=Arrays.stream(VehicleSpec.values()).filter(s->s.id.equals(tag.getString("GarageSpec"))).findFirst().orElse(null);if(spec==null)return false;
        // Preserve UUID and refuse to duplicate an entity already loaded in any dimension.
        if(tag.hasUUID("UUID"))for(var l:level.getServer().getAllLevels())if(l.getEntity(tag.getUUID("UUID"))!=null)return false;
        NgCityEntity car=VehicleContent.type(spec).create(level);if(car==null)return false;car.load(tag.copy());
        var facing=level.getBlockState(terminal).getValue(VehicleServiceBlock.FACING);
        double distance=3+spec.length/2;car.setPos(terminal.getX()+.5+facing.getStepX()*distance,terminal.getY()+.25,terminal.getZ()+.5+facing.getStepZ()*distance);car.setYRot(facing.toYRot());
        var bounds=car.getBoundingBox();
        if(!level.hasChunkAt(car.blockPosition())||!level.mayInteract(p,car.blockPosition())||!level.getWorldBorder().isWithinBounds(bounds)||!level.noCollision(car,bounds.deflate(.001)))return false;
        if(!level.addFreshEntity(car))return false;list.remove(index);setDirty();return true;
    }
    @Override public CompoundTag save(CompoundTag tag){
        ListTag owners=new ListTag();records.forEach((id,list)->{var owner=new CompoundTag();owner.putUUID("Owner",id);ListTag vehicles=new ListTag();list.forEach(t->vehicles.add(t.copy()));owner.put("Vehicles",vehicles);owners.add(owner);});tag.put("Owners",owners);return tag;
    }
    public static VehicleGarageData load(CompoundTag tag){
        var data=new VehicleGarageData();var owners=tag.getList("Owners",10);
        for(int i=0;i<owners.size();i++){var o=owners.getCompound(i);if(!o.hasUUID("Owner"))continue;var list=o.getList("Vehicles",10);var saved=data.entries(o.getUUID("Owner"));for(int j=0;j<Math.min(9,list.size());j++)saved.add(list.getCompound(j).copy());}return data;
    }
}
