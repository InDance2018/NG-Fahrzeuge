package de.nevergate.fahrzeuge;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class VehicleLiftEntity extends BlockEntity {
    private UUID carId,anchor=UUID.randomUUID();private Vec3 base=Vec3.ZERO;
    public VehicleLiftEntity(BlockPos pos,BlockState state){super(VehicleContent.LIFT_ENTITY.get(),pos,state);}
    public NgCityEntity car(){return level instanceof net.minecraft.server.level.ServerLevel l&&carId!=null&&l.getEntity(carId) instanceof NgCityEntity c?c:null;}
    public boolean use(Player p){
        if(level==null||level.isClientSide||!p.isAlive()||p.isSpectator()||!p.getAbilities().mayBuild||!level.mayInteract(p,worldPosition)||p.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY(),worldPosition.getZ()+.5)>64)return false;
        var car=car();if(car!=null){
            if(!car.equipment.authorized(p))return false;
            if(p.getMainHandItem().is(VehicleContent.REPAIR_TOOL.get()))return car.systems.repair(p,p.getMainHandItem());
            var box=car.getBoundingBox().move(base.subtract(car.position()));if(!level.noCollision(car,box))return false;
            release();return true;
        }
        if(carId!=null)return false;
        var list=level.getEntitiesOfClass(NgCityEntity.class,new net.minecraft.world.phys.AABB(worldPosition).inflate(3),c->c.systems.serviceAllowed(p)&&c.equipment.authorized(p)&&!c.isVehicle()&&!c.systems.comfort.connected()&&!c.systems.attached()&&!c.equipment.hasOpeners());
        if(list.isEmpty())return false;list.sort(java.util.Comparator.comparingDouble(c->c.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY(),worldPosition.getZ()+.5)));car=list.get(0);
        Vec3 parked=new Vec3(worldPosition.getX()+.5,worldPosition.getY()+.25,worldPosition.getZ()+.5);Vec3 raised=parked.add(0,1.5,0);if(!level.noCollision(car,car.getBoundingBox().move(raised.subtract(car.position()))))return false;
        base=parked;carId=car.getUUID();car.systems.comfort.lift(worldPosition);car.systems.comfort.parent(anchor,3);car.setPos(raised);level.setBlock(worldPosition,getBlockState().setValue(VehicleServiceBlock.RAISED,true),3);setChanged();return true;
    }
    public void release(){var car=car();if(car!=null){car.systems.comfort.parent(null,0);if(level.noCollision(car,car.getBoundingBox().move(base.subtract(car.position()))))car.setPos(base);}carId=null;if(level!=null&&level.getBlockState(worldPosition).is(VehicleContent.LIFT.get()))level.setBlock(worldPosition,getBlockState().setValue(VehicleServiceBlock.RAISED,false),3);setChanged();}
    @Override protected void saveAdditional(CompoundTag tag){super.saveAdditional(tag);tag.putUUID("Anchor",anchor);if(carId!=null)tag.putUUID("Car",carId);tag.putDouble("X",base.x);tag.putDouble("Y",base.y);tag.putDouble("Z",base.z);}
    @Override public void load(CompoundTag tag){super.load(tag);if(tag.hasUUID("Anchor"))anchor=tag.getUUID("Anchor");carId=tag.hasUUID("Car")?tag.getUUID("Car"):null;base=new Vec3(tag.getDouble("X"),tag.getDouble("Y"),tag.getDouble("Z"));}
}
