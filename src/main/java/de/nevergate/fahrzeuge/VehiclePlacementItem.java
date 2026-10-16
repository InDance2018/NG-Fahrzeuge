package de.nevergate.fahrzeuge;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public final class VehiclePlacementItem extends Item {
    public VehiclePlacementItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player == null || context.getClickedFace() != Direction.UP || stack.isEmpty()
                || !player.getAbilities().mayBuild
                || !level.mayInteract(player, context.getClickedPos())
                || !player.mayUseItemAt(context.getClickedPos().above(), Direction.UP, stack)) {
            return InteractionResult.FAIL;
        }
        NgCityEntity vehicle = VehicleContent.NG_CITY.get().create(level);
        if (vehicle == null) return InteractionResult.FAIL;
        // Cardinal placement gives a clear footprint; steering is a later step.
        vehicle.setYRot(Math.round(context.getRotation() / 90F) * 90F);
        vehicle.setPos(context.getClickedPos().getX() + 0.5,
                context.getClickLocation().y, context.getClickedPos().getZ() + 0.5);
        if (stack.hasCustomHoverName()) vehicle.setCustomName(stack.getHoverName());
        if (!level.getWorldBorder().isWithinBounds(vehicle.getBoundingBox())
                || !level.noCollision(vehicle, vehicle.getBoundingBox().deflate(0.001))) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.no_space"), true);
            }
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide) {
            if (!level.addFreshEntity(vehicle)) return InteractionResult.FAIL;
            if (!player.getAbilities().instabuild) stack.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.ng_fahrzeuge.ng_city"));
        tooltip.add(Component.translatable("tooltip.ng_fahrzeuge.reclaim"));
    }
}
