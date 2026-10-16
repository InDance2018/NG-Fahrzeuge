package de.nevergate.fahrzeuge;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class VehicleWorkbenchBlock extends Block {
    public VehicleWorkbenchBlock(Properties properties) { super(properties); }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (!player.getAbilities().mayBuild || !level.mayInteract(player, pos)) return InteractionResult.FAIL;
        if (!level.isClientSide) player.openMenu(new SimpleMenuProvider(
                (id, inventory, p) -> new VehicleWorkbenchMenu(id, inventory, pos),
                Component.translatable("block.ng_fahrzeuge.vehicle_workbench")));
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
