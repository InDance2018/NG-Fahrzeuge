package de.nevergate.fahrzeuge;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
public final class VehicleDirectoryItem extends Item {
    public VehicleDirectoryItem(Properties p){super(p);}
    @Override public InteractionResultHolder<ItemStack> use(Level l,Player p,InteractionHand h){if(!l.isClientSide)VehicleDirectoryMenu.open(p,0);return InteractionResultHolder.sidedSuccess(p.getItemInHand(h),l.isClientSide);}
}
