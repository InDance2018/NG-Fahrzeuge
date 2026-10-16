package de.nevergate.fahrzeuge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;

/** One-driver test vehicle. All movement is authoritative on the server. */
public final class NgCityEntity extends Entity {
    public static final float WIDTH = 1.8F;
    public static final float LENGTH = 2.6F;
    public static final float HEIGHT = 1.4F;
    public static final float MAX_FORWARD = 0.45F, MAX_REVERSE = 0.18F;
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(
            NgCityEntity.class, EntityDataSerializers.FLOAT);
    private int buttons = VehicleNetwork.BRAKE;
    private long lastInput = Long.MIN_VALUE;
    private int lerpSteps;
    private double targetX, targetY, targetZ;
    private float targetYaw, targetPitch;

    public NgCityEntity(EntityType<? extends NgCityEntity> type, Level level) {
        super(type, level);
        setMaxUpStep(0.6F);
    }

    @Override protected void defineSynchedData() { entityData.define(SPEED, 0F); }
    @Override protected void readAdditionalSaveData(CompoundTag tag) { park(); }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {}

    public Player getDriver() {
        return getFirstPassenger() instanceof Player player ? player : null;
    }

    // Keep vanilla getControllingPassenger() null. Vanilla vehicle position packets
    // must not grant a client authority over this server-driven vehicle.
    public float getDriveSpeed() { return entityData.get(SPEED); }

    public boolean acceptInput(Player player, int input) {
        if (level().isClientSide || isRemoved() || getDriver() != player
                || !player.isAlive() || player.isSpectator() || !player.getAbilities().mayBuild
                || (input & ~VehicleNetwork.VALID_MASK) != 0) return false;
        buttons = input;
        lastInput = level().getGameTime();
        return true;
    }

    /** Limits are blocks per tick. Opposing throttle brakes before changing direction. */
    static float nextSpeed(float speed, int throttle, boolean brake) {
        if (brake) return Mth.approach(speed, 0, 0.045F);
        if (throttle == 0) return Mth.approach(speed, 0, 0.006F);
        if (speed * throttle < 0) return Mth.approach(speed, 0, 0.035F);
        return Mth.clamp(speed + throttle * 0.012F, -MAX_REVERSE, MAX_FORWARD);
    }

    private void park() {
        buttons = VehicleNetwork.BRAKE;
        lastInput = Long.MIN_VALUE;
        entityData.set(SPEED, 0F);
        setDeltaMovement(Vec3.ZERO);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player player && !player.isSpectator()
                && getPassengers().isEmpty() && super.canAddPassenger(passenger);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!level().isClientSide) park();
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity passenger) {
        double yaw = Math.toRadians(getYRot());
        // Lowered driving pose keeps the driver's eye inside this compact cabin.
        return position().add(0.32 * Math.cos(yaw) - 0.10 * Math.sin(yaw), -0.55,
                0.32 * Math.sin(yaw) + 0.10 * Math.cos(yaw));
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction move) {
        if (hasPassenger(passenger)) {
            Vec3 seat = getPassengerRidingPosition(passenger);
            move.accept(passenger, seat.x, seat.y, seat.z);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        double yaw = Math.toRadians(getYRot());
        // Try both sides and both ends, then nearby heights before vanilla's roof fallback.
        double[][] local = {{2.1, 0}, {-2.1, 0}, {0, -2.5}, {0, 2.5}};
        for (double[] offset : local) for (int dy : new int[]{0, 1, -1, 2}) {
            double x = getX() + offset[0] * Math.cos(yaw) - offset[1] * Math.sin(yaw);
            double z = getZ() + offset[0] * Math.sin(yaw) + offset[1] * Math.cos(yaw);
            BlockPos pos = BlockPos.containing(x, getY() + dy, z);
            if (!level().hasChunkAt(pos)) continue;
            Vec3 safe = DismountHelper.findSafeDismountLocation(passenger.getType(), level(), pos, true);
            if (safe != null && !passenger.getDimensions(Pose.STANDING)
                    .makeBoundingBox(safe).intersects(getBoundingBox())) return safe;
        }
        return super.getDismountLocationForPassenger(passenger);
    }

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
        if (level().isClientSide) {
            if (lerpSteps > 0) {
                double t = 1.0 / lerpSteps;
                setPos(Mth.lerp(t, getX(), targetX), Mth.lerp(t, getY(), targetY),
                        Mth.lerp(t, getZ(), targetZ));
                setYRot(getYRot() + Mth.wrapDegrees(targetYaw - getYRot()) / lerpSteps);
                setXRot(getXRot() + (targetPitch - getXRot()) / lerpSteps);
                lerpSteps--;
            }
            return;
        }
        Player driver = getDriver();
        if (driver == null || !driver.isAlive()) park();
        boolean stale = lastInput == Long.MIN_VALUE || level().getGameTime() - lastInput > 10;
        int active = stale ? VehicleNetwork.BRAKE : buttons;
        int throttle = ((active & VehicleNetwork.FORWARD) != 0 ? 1 : 0)
                - ((active & VehicleNetwork.REVERSE) != 0 ? 1 : 0);
        int steering = ((active & VehicleNetwork.RIGHT) != 0 ? 1 : 0)
                - ((active & VehicleNetwork.LEFT) != 0 ? 1 : 0);
        boolean grounded = onGround() || isNoGravity();
        float speed = nextSpeed(getDriveSpeed(), grounded ? throttle : 0,
                (active & VehicleNetwork.BRAKE) != 0);
        if (grounded && Math.abs(speed) > 0.001F && steering != 0) {
            float oldYaw = getYRot();
            setYRot(oldYaw + steering * Math.signum(speed) * 3F
                    * Math.min(1F, Math.abs(speed) / 0.18F));
            if (!level().noCollision(this, getBoundingBox().deflate(0.001))) setYRot(oldYaw);
        }
        double yaw = Math.toRadians(getYRot());
        double vertical = isNoGravity() ? 0 : getDeltaMovement().y - 0.08;
        setDeltaMovement(-Math.sin(yaw) * speed, vertical, Math.cos(yaw) * speed);
        move(MoverType.SELF, getDeltaMovement());
        if (horizontalCollision) speed = 0;
        entityData.set(SPEED, speed);
        setDeltaMovement(0, onGround() ? 0 : vertical * 0.98, 0);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps) {
        targetX = x; targetY = y; targetZ = z;
        targetYaw = yaw; targetPitch = pitch;
        lerpSteps = Math.max(1, steps);
    }

    @Override public double lerpTargetX() { return lerpSteps > 0 ? targetX : getX(); }
    @Override public double lerpTargetY() { return lerpSteps > 0 ? targetY : getY(); }
    @Override public double lerpTargetZ() { return lerpSteps > 0 ? targetZ : getZ(); }
    @Override public float lerpTargetYRot() { return lerpSteps > 0 ? targetYaw : getYRot(); }
    @Override public float lerpTargetXRot() { return lerpSteps > 0 ? targetPitch : getXRot(); }

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
            if (isVehicle()) return InteractionResult.FAIL;
            if (!level().isClientSide) {
                ItemStack returned = getPickResult();
                discard();
                if (!player.getAbilities().instabuild && !player.addItem(returned)) {
                    player.drop(returned, false);
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (player.isSecondaryUseActive()) return InteractionResult.PASS;
        if (!level().isClientSide) {
            if (isVehicle() || !player.startRiding(this)) return InteractionResult.FAIL;
            park();
            player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.controls"), true);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
}
