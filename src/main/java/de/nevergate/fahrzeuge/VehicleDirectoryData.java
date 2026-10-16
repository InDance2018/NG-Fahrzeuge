package de.nevergate.fahrzeuge;

import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Last observations of owned vehicles; never searches or loads chunks. */
public final class VehicleDirectoryData extends SavedData {
    private final Map<UUID,CompoundTag> entries=new HashMap<>();
    public static final Factory<VehicleDirectoryData> FACTORY=new Factory<>(VehicleDirectoryData::new,VehicleDirectoryData::load,net.minecraft.util.datafix.DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
    public static VehicleDirectoryData get(ServerLevel l){return l.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY,"ng_fahrzeuge_directory");}
    public static void update(NgCityEntity c){record(c,false);}
    public static void stored(NgCityEntity c){record(c,true);}
    private static void record(NgCityEntity c,boolean stored){
        if(!(c.level() instanceof ServerLevel l)||c.equipment.owner()==null)return;
        var t=new CompoundTag();t.putUUID("Id",c.getUUID());t.putUUID("Owner",c.equipment.owner());t.putString("Name",c.getName().getString());t.putString("Type",c.spec().id);t.putString("Dimension",l.dimension().location().toString());t.putDouble("X",c.getX());t.putDouble("Y",c.getY());t.putDouble("Z",c.getZ());t.putLong("Seen",l.getGameTime());t.putBoolean("Stored",stored);
        var d=get(l);d.entries.put(c.getUUID(),t);d.setDirty();
    }
    public static void remove(NgCityEntity c){if(c.level() instanceof ServerLevel l&&get(l).entries.remove(c.getUUID())!=null)get(l).setDirty();}
    public List<CompoundTag> own(UUID owner){return entries.values().stream().filter(t->t.getUUID("Owner").equals(owner)).sorted(Comparator.comparing(t->t.getString("Name"))).map(CompoundTag::copy).toList();}
    @Override public CompoundTag save(CompoundTag t){var list=new ListTag();entries.values().forEach(e->list.add(e.copy()));t.put("Vehicles",list);return t;}
    public static VehicleDirectoryData load(CompoundTag t){var d=new VehicleDirectoryData();var list=t.getList("Vehicles",10);for(int i=0;i<list.size();i++){var e=list.getCompound(i);if(e.hasUUID("Id")&&e.hasUUID("Owner")&&Double.isFinite(e.getDouble("X"))&&Double.isFinite(e.getDouble("Y"))&&Double.isFinite(e.getDouble("Z")))d.entries.put(e.getUUID("Id"),e.copy());}return d;}
}
