package de.nevergate.fahrzeuge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Parked prototype: one persistent entity, with a footprint matching the model. */
public final class NgCityEntity extends Entity {
    public static final float WIDTH = 1.8F;
    public static final float LENGTH = 2.6F;
    public static final float HEIGHT = 1.4F;

    public NgCityEntity(EntityType<? extends NgCityEntity> type, Level level) {
        super(type, level);
    }

    @Override protected void defineSynchedData() {}
    @Override protected void readAdditionalSaveData(CompoundTag tag) {}
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void setYRot(float yaw) {
        super.setYRot(yaw);
        setBoundingBox(makeBoundingBox());
    }

    @Override
    protected AABB makeBoundingBox() {
        double angle = Math.toRadians(getYRot());
        double x = (Math.abs(Math.cos(angle)) * WIDTH + Math.abs(Math.sin(angle)) * LENGTH) / 2;
        double z = (Math.abs(Math.sin(angle)) * WIDTH + Math.abs(Math.cos(angle)) * LENGTH) / 2;
        return new AABB(getX() - x, getY(), getZ() - z, getX() + x, getY() + HEIGHT, getZ() + z);
    }

    @Override public boolean isPickable() { return !isRemoved(); }
    @Override public boolean canBeCollidedWith() { return !isRemoved(); }
    @Override public boolean canCollideWith(Entity other) {
        return !isRemoved() && (other.canBeCollidedWith() || other.isPushable()) && !isPassengerOfSameVehicle(other);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            double vertical = isNoGravity() ? 0 : getDeltaMovement().y - 0.08;
            setDeltaMovement(0, vertical, 0);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(onGround() ? Vec3.ZERO : new Vec3(0, vertical * 0.98, 0));
        }
    }

    @Override
    public ItemStack getPickResult() {
        ItemStack stack = VehicleContent.NG_CITY_ITEM.get().getDefaultInstance();
        if (hasCustomName()) stack.setHoverName(getCustomName());
        return stack;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (isRemoved() || hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!player.getAbilities().mayBuild || !level().mayInteract(player, blockPosition())) {
            return InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            if (!level().isClientSide) {
                ItemStack returned = getPickResult();
                discard();
                if (!player.getAbilities().instabuild && !player.addItem(returned)) {
                    player.drop(returned, false);
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!level().isClientSide) {
            player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.prototype"), true);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
}
