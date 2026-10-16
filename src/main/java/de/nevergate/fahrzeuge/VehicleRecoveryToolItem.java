package de.nevergate.fahrzeuge;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class VehicleRecoveryToolItem extends Item {
    private final String hint;
    public VehicleRecoveryToolItem(Properties properties,String hint){super(properties);this.hint=hint;}
    @Override public void appendHoverText(ItemStack stack,@Nullable Level level,List<Component> lines,TooltipFlag flag){
        lines.add(Component.translatable("tooltip.ng_fahrzeuge."+hint));
    }
}
