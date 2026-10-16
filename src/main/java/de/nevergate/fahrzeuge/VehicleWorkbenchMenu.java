package de.nevergate.fahrzeuge;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class VehicleWorkbenchMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    public VehicleWorkbenchMenu(int id, Inventory inventory) { this(id, inventory, null); }
    public VehicleWorkbenchMenu(int id, Inventory inventory, BlockPos pos) {
        super(VehicleContent.WORKBENCH_MENU.get(), id);
        this.pos = pos;
        for (int row=0;row<3;row++) for(int col=0;col<9;col++)
            addSlot(new Slot(inventory, col+row*9+9, 77+col*18, 126+row*18));
        for (int col=0;col<9;col++) addSlot(new Slot(inventory, col, 77+col*18, 184));
    }
    @Override public boolean stillValid(Player player) {
        return player.isAlive() && !player.isSpectator() && player.getAbilities().mayBuild
                && (player.level().isClientSide || pos != null
                && player.distanceToSqr(pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5)<=64
                && player.level().getBlockState(pos).is(VehicleContent.WORKBENCH.get())
                && player.level().mayInteract(player, pos));
    }
    @Override public boolean clickMenuButton(Player player, int button) {
        if (!stillValid(player) || button < 0 || button >= VehicleSpec.values().length) return false;
        boolean built = VehicleAssembly.assemble(player, VehicleSpec.values()[button]);
        if (!built) player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.materials"), true);
        broadcastChanges();
        return built;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (!stillValid(player) || index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot=slots.get(index); if(!slot.hasItem())return ItemStack.EMPTY;
        ItemStack source=slot.getItem(), copy=source.copy();
        if(index<27 ? !moveItemStackTo(source,27,36,false) : !moveItemStackTo(source,0,27,false))return ItemStack.EMPTY;
        if(source.isEmpty())slot.setByPlayer(ItemStack.EMPTY);else slot.setChanged();
        return copy;
    }
}
