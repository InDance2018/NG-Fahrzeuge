package de.nevergate.fahrzeuge;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

/** Persistent ownership and cargo. No client-selected inventory or UUID is trusted. */
public final class VehicleEquipment {
    private final NgCityEntity car;
    private UUID owner;
    private UUID keyId = UUID.randomUUID();
    private int openers;
    public final SimpleContainer cargo;

    VehicleEquipment(NgCityEntity car) {
        this.car = car;
        cargo = new SimpleContainer(rows() * 9) {
            @Override public boolean stillValid(Player player) { return canOpen(player); }
            @Override public void startOpen(Player player) { openers++; }
            @Override public void stopOpen(Player player) { openers = Math.max(0, openers - 1); }
        };
    }
    public int rows() { return switch(car.spec()) { case CITY -> 1; case FAMILY -> 3; case TRANSPORTER -> 6; }; }
    public UUID owner() { return owner; }
    public UUID keyId() { return keyId; }
    public void claim(Player player) { if (!car.level().isClientSide && owner == null) owner = player.getUUID(); }
    public boolean isOwner(Player player) { return owner == null || owner.equals(player.getUUID()); }
    public boolean matches(ItemStack key) {
        return key.is(VehicleContent.KEY.get()) && key.hasTag() && key.getTag().hasUUID("VehicleKeyId")
                && keyId.equals(key.getTag().getUUID("VehicleKeyId"));
    }
    public boolean authorized(Player player) {
        if (isOwner(player)) return true;
        for (ItemStack key : player.getInventory().items) if (matches(key)) return true;
        return matches(player.getOffhandItem());
    }
    public boolean bindKey(Player player, ItemStack key) {
        if (car.level().isClientSide || !isOwner(player) || !key.is(VehicleContent.KEY.get())
                || key.getOrCreateTag().hasUUID("VehicleKeyId")) return false;
        claim(player);
        key.getOrCreateTag().putUUID("VehicleKeyId", keyId);
        key.setHoverName(Component.translatable("item.ng_fahrzeuge.bound_key", car.getName()));
        return true;
    }
    public boolean canOpen(Player player) {
        return !car.isRemoved() && player.isAlive() && !player.isSpectator()
                && player.getAbilities().mayBuild && authorized(player)
                && player.distanceToSqr(car) <= 64 && car.isParkingBraked() && !car.engineRunning();
    }
    public boolean hasOpeners() { return openers > 0; }
    public boolean open(Player player) {
        if (!canOpen(player)) return false;
        claim(player);
        if (!car.level().isClientSide) player.openMenu(new SimpleMenuProvider((id, inventory, p) -> {
            MenuType<?> type = switch(rows()) { case 1 -> MenuType.GENERIC_9x1; case 3 -> MenuType.GENERIC_9x3; default -> MenuType.GENERIC_9x6; };
            return new ChestMenu(type, id, inventory, cargo, rows()) {
                @Override public boolean stillValid(Player who) { return canOpen(who); }
                @Override public void clicked(int slot, int button, ClickType click, Player who) {
                    if (stillValid(who)) super.clicked(slot, button, click, who);
                }
                @Override public ItemStack quickMoveStack(Player who, int slot) {
                    return stillValid(who) ? super.quickMoveStack(who, slot) : ItemStack.EMPTY;
                }
            };
        }, Component.translatable("container.ng_fahrzeuge.cargo", car.getName())));
        return true;
    }
    public void save(CompoundTag tag) {
        if (owner != null) tag.putUUID("VehicleOwner", owner);
        tag.putUUID("VehicleKeyId", keyId);
        net.minecraft.nbt.ListTag items = new net.minecraft.nbt.ListTag();
        for (int i=0;i<cargo.getContainerSize();i++) if(!cargo.getItem(i).isEmpty()) {
            CompoundTag item = cargo.getItem(i).save(new CompoundTag()); item.putInt("Slot",i); items.add(item);
        }
        tag.put("VehicleCargo", items);
    }
    public void load(CompoundTag tag) {
        owner = tag.hasUUID("VehicleOwner") ? tag.getUUID("VehicleOwner") : null;
        if (tag.hasUUID("VehicleKeyId")) keyId = tag.getUUID("VehicleKeyId");
        cargo.clearContent();
        var items = tag.getList("VehicleCargo", 10);
        for (int i=0;i<items.size();i++) {
            CompoundTag item=items.getCompound(i);int slot=item.getInt("Slot");
            if(slot>=0 && slot<cargo.getContainerSize())cargo.setItem(slot,ItemStack.of(item));
        }
    }
}
