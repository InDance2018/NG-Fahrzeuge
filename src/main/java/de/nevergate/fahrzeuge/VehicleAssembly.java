package de.nevergate.fahrzeuge;

import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** Assembly recipes shared by the workbench screen and authoritative server transaction. */
public final class VehicleAssembly {
    public record Cost(Item item, int count) {}
    public static List<Cost> costs(VehicleSpec spec) {
        int scale = spec.ordinal();
        return List.of(new Cost(Items.IRON_INGOT, 24 + scale * 12), new Cost(Items.COPPER_INGOT, 8 + scale * 4),
                new Cost(Items.GLASS, 6 + scale * 2), new Cost(Items.BLACK_WOOL, 4),
                new Cost(Items.REDSTONE, 4 + scale * 2));
    }
    public static boolean assemble(Player player, VehicleSpec spec) {
        if (player.level().isClientSide || !player.isAlive() || player.isSpectator()
                || !player.getAbilities().mayBuild || player.getInventory().getFreeSlot() < 0) return false;
        if (!player.getAbilities().instabuild) {
            for (Cost cost : costs(spec)) if (player.getInventory().countItem(cost.item()) < cost.count()) return false;
            // Check every cost before consuming anything; one server thread owns the inventory.
            for (Cost cost : costs(spec)) {
                int left = cost.count();
                for (int i = 0; i < player.getInventory().getContainerSize() && left > 0; i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (stack.is(cost.item())) { int take = Math.min(left, stack.getCount()); stack.shrink(take); left -= take; }
                }
            }
        }
        player.getInventory().add(new ItemStack(VehicleContent.item(spec)));
        player.getInventory().setChanged();
        return true;
    }
    private VehicleAssembly() {}
}
