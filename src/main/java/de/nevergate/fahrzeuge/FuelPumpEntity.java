package de.nevergate.fahrzeuge;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class FuelPumpEntity extends BlockEntity {
    private float fuel;
    public FuelPumpEntity(BlockPos pos,BlockState state){super(VehicleContent.PUMP_ENTITY.get(),pos,state);}
    public float litres(){return fuel;}
    public float fill(float amount){
        if(level!=null&&level.isClientSide||!Float.isFinite(amount)||amount<=0)return 0;
        float added=Math.min(amount,500-fuel);fuel+=added;setChanged();return added;
    }
    public float dispense(NgCityEntity car,Player player){
        if(level==null||level.isClientSide||car.level()!=level||car.distanceToSqr(worldPosition.getX()+.5,worldPosition.getY(),worldPosition.getZ()+.5)>36
                ||!level.mayInteract(player,worldPosition)||!car.systems.serviceAllowed(player))return 0;
        float used=car.systems.refuel(Math.min(10,fuel));fuel-=used;setChanged();return used;
    }
    @Override protected void saveAdditional(CompoundTag tag){super.saveAdditional(tag);tag.putFloat("Fuel",fuel);}
    @Override public void load(CompoundTag tag){super.load(tag);float n=tag.getFloat("Fuel");fuel=Float.isFinite(n)?Math.max(0,Math.min(500,n)):0;}
    @Override public CompoundTag getUpdateTag(){return saveWithoutMetadata();}
    @Override public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket(){return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);}
}
