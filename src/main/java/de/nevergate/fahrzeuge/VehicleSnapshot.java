package de.nevergate.fahrzeuge;
import java.util.*;
import net.minecraft.world.phys.*;
/** Read-only view of an already loaded vehicle; no lookup, chunk loading, or mutators. */
public record VehicleSnapshot(UUID id,String dimension,Vec3 position,AABB bounds,String type,UUID owner,UUID driver,boolean parked) {
    public static Optional<VehicleSnapshot> of(NgCityEntity c){if(c==null||c.isRemoved()||c.level().isClientSide)return Optional.empty();var d=c.observedDriver();return Optional.of(new VehicleSnapshot(c.getUUID(),c.level().dimension().location().toString(),c.position(),c.getBoundingBox(),c.spec().id,c.equipment.owner(),d==null?null:d.getUUID(),c.isParkingBraked()&&Math.abs(c.getDriveSpeed())<.001F));}
}
