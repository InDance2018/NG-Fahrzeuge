package de.nevergate.fahrzeuge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.context.BlockPlaceContext;

public final class VehicleServiceBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING=HorizontalDirectionalBlock.FACING;
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty RAISED=net.minecraft.world.level.block.state.properties.BooleanProperty.create("raised");
    public final int service; // 0 dealer, 1 garage, 2 workshop lift
    public VehicleServiceBlock(Properties props,int service){super(props);this.service=service;registerDefaultState(stateDefinition.any().setValue(FACING,Direction.NORTH).setValue(RAISED,false));}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING,RAISED);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx){return defaultBlockState().setValue(FACING,ctx.getHorizontalDirection().getOpposite());}
    @Override public RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return service==2?new VehicleLiftEntity(pos,state):null;}
    @Override public net.minecraft.world.phys.shapes.VoxelShape getShape(BlockState state,net.minecraft.world.level.BlockGetter level,BlockPos pos,net.minecraft.world.phys.shapes.CollisionContext ctx){return service==2?Block.box(0,0,0,16,4,16):super.getShape(state,level,pos,ctx);}
    @Override public InteractionResult use(BlockState state,Level level,BlockPos pos,Player p,InteractionHand hand,BlockHitResult hit){
        if(hand!=InteractionHand.MAIN_HAND||!p.getAbilities().mayBuild||p.isSpectator()||!level.mayInteract(p,pos))return InteractionResult.FAIL;
        if(!level.isClientSide){if(service==2){var lift=(VehicleLiftEntity)level.getBlockEntity(pos);if(lift==null||!lift.use(p))return InteractionResult.FAIL;}
            else VehicleServiceMenu.open(p,pos,service);}
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
        if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof VehicleLiftEntity lift)lift.release();super.onRemove(state,level,pos,next,moving);
    }
}
