package de.nevergate.fahrzeuge;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class FuelPumpBlock extends BaseEntityBlock {
    public FuelPumpBlock(Properties properties){super(properties);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FuelPumpEntity(pos,state);}
    @Override public RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public InteractionResult use(BlockState state,Level level,BlockPos pos,Player p,InteractionHand hand,BlockHitResult hit){
        if(hand!=InteractionHand.MAIN_HAND||!p.getAbilities().mayBuild||!level.mayInteract(p,pos))return InteractionResult.FAIL;
        if(!level.isClientSide&&level.getBlockEntity(pos) instanceof FuelPumpEntity pump){
            var held=p.getMainHandItem();
            if(held.is(VehicleContent.FUEL_CAN.get())){
                float added=pump.fill(FuelCanItem.litres(held));if(!p.getAbilities().instabuild)FuelCanItem.consume(held,added);
            }else{
                var cars=level.getEntitiesOfClass(NgCityEntity.class,new net.minecraft.world.phys.AABB(pos).inflate(6),
                        car->car.systems.serviceAllowed(p)&&car.systems.fuel()<car.systems.tankCapacity());
                cars.sort(java.util.Comparator.comparingDouble(car->car.distanceToSqr(pos.getX()+.5,pos.getY(),pos.getZ()+.5)));
                if(!cars.isEmpty())pump.dispense(cars.get(0),p);
            }
            p.displayClientMessage(Component.translatable("message.ng_fahrzeuge.pump",String.format(java.util.Locale.ROOT,"%.2f",pump.litres())),true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
