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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import java.util.Optional;
import java.util.UUID;

/** One-driver test vehicle. All movement is authoritative on the server. */
public final class NgCityEntity extends Entity {
    public static final float WIDTH = 1.8F;
    public static final float LENGTH = 2.6F;
    public static final float HEIGHT = 1.4F;
    public static final float MAX_FORWARD = 0.45F, MAX_REVERSE = 0.18F;
    private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(
            NgCityEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> PARKING = SynchedEntityData.defineId(NgCityEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LIGHTS = SynchedEntityData.defineId(NgCityEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BRAKING = SynchedEntityData.defineId(NgCityEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(NgCityEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> DRIVER = SynchedEntityData.defineId(NgCityEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private final VehicleSpec spec;
    private int buttons = VehicleNetwork.BRAKE;
    private long lastInput = Long.MIN_VALUE;
    private int lerpSteps;
    private double targetX, targetY, targetZ;
    private float targetYaw, targetPitch;

    public NgCityEntity(EntityType<? extends NgCityEntity> type, Level level) {
        this(type, level, VehicleSpec.CITY);
    }

    public NgCityEntity(EntityType<? extends NgCityEntity> type, Level level, VehicleSpec spec) {
        super(type, level);
        this.spec = spec;
        entityData.set(COLOR, spec.defaultColor.getId());
        refreshDimensions();
        setMaxUpStep(0.6F);
    }

    @Override protected void defineSynchedData() {
        entityData.define(SPEED, 0F); entityData.define(PARKING, true);
        entityData.define(LIGHTS, false); entityData.define(BRAKING, false);
        entityData.define(COLOR, VehicleSpec.CITY.defaultColor.getId());
        entityData.define(DRIVER, Optional.empty());
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        resetMotion(); entityData.set(DRIVER, Optional.empty()); loadEquipment(tag);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { saveEquipment(tag); }

    public VehicleSpec spec() { return spec == null ? VehicleSpec.CITY : spec; }
    public boolean isParkingBraked() { return entityData.get(PARKING); }
    public boolean lightsOn() { return entityData.get(LIGHTS); }
    public boolean isBraking() { return entityData.get(BRAKING); }
    public int color() { return entityData.get(COLOR); }

    public void saveEquipment(CompoundTag tag) {
        tag.putInt("VehicleColor", color());
        tag.putBoolean("ParkingBrake", isParkingBraked());
        tag.putBoolean("VehicleLights", lightsOn());
    }

    public void loadEquipment(CompoundTag tag) {
        int color = tag.contains("VehicleColor", 3) ? tag.getInt("VehicleColor") : spec().defaultColor.getId();
        entityData.set(COLOR, color >= 0 && color < 16 ? color : spec().defaultColor.getId());
        entityData.set(PARKING, !tag.contains("ParkingBrake", 1) || tag.getBoolean("ParkingBrake"));
        entityData.set(LIGHTS, tag.getBoolean("VehicleLights"));
    }

    /** Actions are accepted only from this vehicle's seated driver. */
    public boolean applyAction(Player player, int action) {
        if (level().isClientSide || getDriver() != player || !player.isAlive()
                || player.isSpectator() || !player.getAbilities().mayBuild) return false;
        if (action == VehicleNetwork.PARK_ACTION) entityData.set(PARKING, !isParkingBraked());
        else if (action == VehicleNetwork.LIGHT_ACTION) entityData.set(LIGHTS, !lightsOn());
        else return false;
        return true;
    }

    public Player getDriver() {
        Optional<UUID> id = entityData.get(DRIVER);
        if (id.isEmpty()) return null;
        for (Entity passenger : getPassengers()) {
            if (passenger instanceof Player player && player.getUUID().equals(id.get())) return player;
        }
        return null;
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

    private void resetMotion() {
        buttons = VehicleNetwork.BRAKE;
        lastInput = Long.MIN_VALUE;
        entityData.set(SPEED, 0F);
        entityData.set(BRAKING, false);
        setDeltaMovement(Vec3.ZERO);
    }

    private void park() { resetMotion(); entityData.set(PARKING, true); }

    @Override public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(spec().width, spec().height);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player player && !player.isSpectator()
                && !isRemoved() && getPassengers().size() < spec().seats;
    }

    @Override protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (!level().isClientSide && passenger instanceof Player && entityData.get(DRIVER).isEmpty()) {
            entityData.set(DRIVER, Optional.of(passenger.getUUID()));
            resetMotion();
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        boolean driver = entityData.get(DRIVER).filter(passenger.getUUID()::equals).isPresent();
        super.removePassenger(passenger);
        if (!level().isClientSide && driver) {
            entityData.set(DRIVER, Optional.empty()); park();
        }
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity passenger) {
        double yaw = Math.toRadians(getYRot());
        int seat = 0;
        if (getDriver() != passenger) {
            seat = 1;
            for (Entity other : getPassengers()) {
                if (other == passenger) break;
                if (other != getDriver()) seat++;
            }
        }
        double x = seat % 2 == 0 ? 0.32 : -0.32;
        double z = spec() == VehicleSpec.TRANSPORTER ? 1.05 : (seat < 2 ? 0.10 : -0.85);
        double y = spec() == VehicleSpec.TRANSPORTER ? 0.05 : (spec() == VehicleSpec.FAMILY ? -0.40 : -0.55);
        return position().add(x * Math.cos(yaw) - z * Math.sin(yaw), y,
                x * Math.sin(yaw) + z * Math.cos(yaw));
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
        double side = spec().width / 2 + 1.2, end = spec().length / 2 + 1.2;
        double[][] local = {{side, 0}, {-side, 0}, {0, -end}, {0, end}};
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
        double x = (Math.abs(Math.cos(angle)) * spec().width + Math.abs(Math.sin(angle)) * spec().length) / 2;
        double z = (Math.abs(Math.sin(angle)) * spec().width + Math.abs(Math.cos(angle)) * spec().length) / 2;
        return new AABB(getX() - x, getY(), getZ() - z, getX() + x, getY() + spec().height, getZ() + z);
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
        boolean braking = driver != null && ((active & VehicleNetwork.BRAKE) != 0
                || throttle * getDriveSpeed() < 0);
        entityData.set(BRAKING, braking);
        float speed = nextSpeed(getDriveSpeed(), grounded ? throttle : 0,
                isParkingBraked() || (active & VehicleNetwork.BRAKE) != 0);
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
        ItemStack stack = VehicleContent.item(spec()).getDefaultInstance();
        saveEquipment(stack.getOrCreateTag());
        if (hasCustomName()) stack.setHoverName(getCustomName());
        return stack;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (isRemoved() || hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!player.getAbilities().mayBuild || !level().mayInteract(player, blockPosition())) {
            return InteractionResult.FAIL;
        }
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof DyeItem dye) {
            if (isVehicle() && getDriver() != player) return InteractionResult.FAIL;
            if (!level().isClientSide && color() != dye.getDyeColor().getId()) {
                entityData.set(COLOR, dye.getDyeColor().getId());
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
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
            if (!canAddPassenger(player) || !player.startRiding(this)) return InteractionResult.FAIL;
            player.displayClientMessage(Component.translatable(getDriver() == player
                    ? "message.ng_fahrzeuge.controls" : "message.ng_fahrzeuge.passenger"), true);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
}
