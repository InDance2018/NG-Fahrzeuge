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
import java.util.Set;
import java.util.HashSet;

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
    private static final EntityDataAccessor<Boolean> ENGINE = SynchedEntityData.defineId(NgCityEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LOCKED = SynchedEntityData.defineId(NgCityEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SIGNAL = SynchedEntityData.defineId(NgCityEntity.class, EntityDataSerializers.INT);
    private final VehicleSpec spec;
    public final VehicleEquipment equipment;
    public final VehicleSystems systems;
    private static final EntityDataAccessor<CompoundTag> SYSTEMS = SynchedEntityData.defineId(NgCityEntity.class,EntityDataSerializers.COMPOUND_TAG);
    public CompoundTag systemTag(){return entityData.get(SYSTEMS);}
    public void syncSystems(CompoundTag tag){entityData.set(SYSTEMS,tag);}
    public void stopEngine(){entityData.set(ENGINE,false);if(systems!=null)systems.cancelCruise();}
    private long lastHorn = Long.MIN_VALUE, lastAction = Long.MIN_VALUE;
    private int lastActionKind = -1;
    private final Set<UUID> knownPassengers = new HashSet<>();
    private int steeringInput;public int steeringInput(){return steeringInput;}
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
        this.equipment = new VehicleEquipment(this);
        this.systems = new VehicleSystems(this); systems.sync();
        if(spec==VehicleSpec.BUS)addTag("ng_bus");
        entityData.set(COLOR, spec.defaultColor.getId());
        refreshDimensions();
        setMaxUpStep(0.6F);
    }

    @Override protected void defineSynchedData() {
        entityData.define(SPEED, 0F); entityData.define(PARKING, true);
        entityData.define(LIGHTS, false); entityData.define(BRAKING, false);
        entityData.define(COLOR, VehicleSpec.CITY.defaultColor.getId());
        entityData.define(DRIVER, Optional.empty());
        entityData.define(SYSTEMS,new CompoundTag());
        entityData.define(ENGINE, false); entityData.define(LOCKED, false); entityData.define(SIGNAL, 0);
    }
    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        resetMotion(); entityData.set(DRIVER, Optional.empty()); knownPassengers.clear(); loadEquipment(tag);
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) { saveEquipment(tag); }

    public VehicleSpec spec() { return spec == null ? VehicleSpec.CITY : spec; }
    public boolean isParkingBraked() { return entityData.get(PARKING); }
    public boolean lightsOn() { return entityData.get(LIGHTS); }
    public boolean isBraking() { return entityData.get(BRAKING); }
    public boolean paint(int color){if(level().isClientSide||color<0||color>15||color==color())return false;entityData.set(COLOR,color);return true;}
    public void clearSignal(){entityData.set(SIGNAL,0);}
    public int color() { return entityData.get(COLOR); }
    public boolean engineRunning() { return entityData.get(ENGINE); }
    public boolean isLocked() { return entityData.get(LOCKED); }
    public int signal() { return entityData.get(SIGNAL); }
    public boolean indicatorLit(boolean left) {
        return (signal() == 3 || signal() == (left ? 1 : 2)) && level().getGameTime() % 20 < 10;
    }
    public boolean toggleLock(Player player) {
        if (level().isClientSide || !equipment.authorized(player) || isVehicle()
                || equipment.hasOpeners() || Math.abs(getDriveSpeed()) > .001F) return false;
        park(); entityData.set(LOCKED, !isLocked());
        level().playSound(null, getX(), getY(), getZ(), VehicleSounds.LOCK.get(), net.minecraft.sounds.SoundSource.PLAYERS, .6F, 1F);
        player.displayClientMessage(Component.translatable(isLocked() ? "message.ng_fahrzeuge.locked" : "message.ng_fahrzeuge.unlocked"), true);
        return true;
    }

    public void saveEquipment(CompoundTag tag) {
        tag.putInt("VehicleColor", color());
        tag.putBoolean("ParkingBrake", isParkingBraked());
        tag.putBoolean("VehicleLights", lightsOn());
        tag.putBoolean("VehicleLocked", isLocked());
        tag.putInt("VehicleSignal", signal());
        equipment.save(tag);
        CompoundTag state=new CompoundTag();systems.save(state);tag.put("VehicleSystems",state);
    }

    public void loadEquipment(CompoundTag tag) {
        int color = tag.contains("VehicleColor", 3) ? tag.getInt("VehicleColor") : spec().defaultColor.getId();
        entityData.set(COLOR, color >= 0 && color < 16 ? color : spec().defaultColor.getId());
        entityData.set(PARKING, !tag.contains("ParkingBrake", 1) || tag.getBoolean("ParkingBrake"));
        entityData.set(LIGHTS, tag.getBoolean("VehicleLights"));
        entityData.set(LOCKED, tag.getBoolean("VehicleLocked"));
        entityData.set(SIGNAL, Mth.clamp(tag.getInt("VehicleSignal"), 0, 3));
        entityData.set(ENGINE, false);
        equipment.load(tag);
        systems.load(tag.getCompound("VehicleSystems"));
    }

    /** Actions are accepted only from this vehicle's seated driver. */
    public boolean applyAction(Player player, int action) {
        if(!level().isClientSide&&action>=19&&hasPassenger(player)&&player.isAlive()&&!player.isSpectator()&&player.getAbilities().mayBuild)return systems.expansion.action(player,action);
        if (level().isClientSide || getDriver() != player || !player.isAlive()
                || player.isSpectator() || !player.getAbilities().mayBuild) return false;
        if (action == VehicleNetwork.CARGO_ACTION) {
            boolean opened=equipment.open(player);
            if(!opened)player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.park_first"),true);
            return opened;
        }
        if(action>=VehicleNetwork.DOOR_ACTION&&action<=VehicleNetwork.ROOF_ACTION)return systems.action(player,action);
        if (equipment.hasOpeners()) return false;
        long now = level().getGameTime();
        if (action == VehicleNetwork.ENGINE_ACTION && lastActionKind == action && lastAction != Long.MIN_VALUE && now - lastAction < 4) return false;
        if (action == VehicleNetwork.ENGINE_ACTION) {
            if (!equipment.authorized(player)) {
                player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.key_required"),true);return false;
            }
            if(!engineRunning()&&!systems.canRun()){
                player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.cannot_start"),true);return false;
            }
            if(!engineRunning())systems.comfort.startCost();
            systems.closeDoors();
            entityData.set(ENGINE, !engineRunning());
            if (engineRunning()) level().playSound(null, getX(), getY(), getZ(), VehicleSounds.START.get(), net.minecraft.sounds.SoundSource.NEUTRAL, .7F, spec() == VehicleSpec.TRANSPORTER ? .8F : 1F);
        } else if (action == VehicleNetwork.LEFT_ACTION || action == VehicleNetwork.RIGHT_ACTION || action == VehicleNetwork.HAZARD_ACTION) {
            int next = action - VehicleNetwork.LEFT_ACTION + 1;
            entityData.set(SIGNAL, signal() == next ? 0 : next);systems.expansion.signalStarted();
        } else if (action == VehicleNetwork.HORN_ACTION) {
            if (lastHorn != Long.MIN_VALUE && now - lastHorn < 12) return false;
            lastHorn = now;
            level().playSound(null, getX(), getY(), getZ(), VehicleSounds.HORN.get(), net.minecraft.sounds.SoundSource.NEUTRAL, 1F, spec() == VehicleSpec.TRANSPORTER ? .8F : 1F);
        } else if (action == VehicleNetwork.PARK_ACTION) entityData.set(PARKING, !isParkingBraked());
        else if (action == VehicleNetwork.LIGHT_ACTION) {systems.expansion.cycleLights();entityData.set(LIGHTS,systems.expansion.lightMode()>0);}
        else return false;
        lastAction = now; lastActionKind = action;
        return true;
    }

    public Player getDriver() {
        if (!level().isClientSide) reconcilePassengers();
        return observedDriver();
    }
    /** Observes the last reconciled driver without changing vehicle state. */
    public Player observedDriver() {
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

    private void park() { resetMotion(); entityData.set(PARKING, true); stopEngine(); }

    @Override public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(spec().width, spec().height);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player player && !player.isSpectator()
                && !isRemoved() && !systems.comfort.restrained() && (!isLocked() || equipment.authorized(player)) && getPassengers().size() < spec().seats;
    }

    @Override protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (!level().isClientSide) reconcilePassengers();
    }

    @Override protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!level().isClientSide) reconcilePassengers();
    }

    /** Arclight bypasses mod add/removePassenger overrides; also check actual membership. */
    private void reconcilePassengers() {
        Set<UUID> current = new HashSet<>();
        for (Entity passenger : getPassengers()) current.add(passenger.getUUID());
        Optional<UUID> driver = entityData.get(DRIVER);
        if (driver.isPresent() && !current.contains(driver.get())) {
            entityData.set(DRIVER, Optional.empty());
            park();
        }
        if (entityData.get(DRIVER).isEmpty()) {
            for (Entity passenger : getPassengers()) {
                if (passenger instanceof Player && !knownPassengers.contains(passenger.getUUID())) {
                    entityData.set(DRIVER, Optional.of(passenger.getUUID()));
                    resetMotion();
                    break;
                }
            }
        }
        knownPassengers.clear();
        knownPassengers.addAll(current);
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
        if(spec()==VehicleSpec.BUS){x=seat==0?.65:(seat%2==0?.65:-.65);double z=seat==0?2.9:1.8-((seat-1)/2)*.9;return position().add(x*Math.cos(yaw)-z*Math.sin(yaw),.45,x*Math.sin(yaw)+z*Math.cos(yaw)).add(seat==0?systems.expansion.seatOffsetWorld():Vec3.ZERO);}
        if(spec()==VehicleSpec.MOTORCYCLE){double z=seat==0?.15:-.55;return position().add(-z*Math.sin(yaw),-.62,z*Math.cos(yaw)).add(seat==0?systems.expansion.seatOffsetWorld():Vec3.ZERO);}
        double z = spec()==VehicleSpec.PICKUP?.8:spec() == VehicleSpec.TOW_TRUCK?1.6:spec() == VehicleSpec.TRANSPORTER ? 1.05 : (seat < 2 ? 0.10 : -0.85);
        double y = (spec() == VehicleSpec.TRANSPORTER||spec()==VehicleSpec.TOW_TRUCK) ? 0.05 : (spec() == VehicleSpec.FAMILY ? -0.40 : -0.55);
        return position().add(x * Math.cos(yaw) - z * Math.sin(yaw), y,
                x * Math.sin(yaw) + z * Math.cos(yaw)).add(seat==0?systems.expansion.seatOffsetWorld():Vec3.ZERO);
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

    @Override public boolean hurt(net.minecraft.world.damagesource.DamageSource source,float amount){
        if(isInvulnerableTo(source)||level().isClientSide)return false;
        systems.damage(amount*2);return true;
    }
    @Override public void remove(RemovalReason reason){if(reason==RemovalReason.DISCARDED&&!level().isClientSide&&systems!=null)VehicleDirectoryData.remove(this);super.remove(reason);}
    @Override public boolean isPickable() { return !isRemoved(); }
    @Override public boolean canBeCollidedWith() { return !isRemoved(); }
    @Override public boolean canCollideWith(Entity other) {
        return !isRemoved() && !(other instanceof NgCityEntity c && VehicleRecovery.related(this,c)) && !(other instanceof TrailerEntity && other.getUUID().equals(systems.trailerId())) && (other.canBeCollidedWith() || other.isPushable()) && !isPassengerOfSameVehicle(other);
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
        systems.expansion.reconcileBelts();
        if(tickCount%20==0)VehicleDirectoryData.update(this);
        if(VehicleRecovery.tick(this))return;
        if (signal() != 0 && level().getGameTime() % 10 == 0)
            level().playSound(null, getX(), getY(), getZ(), VehicleSounds.INDICATOR.get(), net.minecraft.sounds.SoundSource.NEUTRAL, .18F, level().getGameTime() % 20 == 0 ? 1F : .85F);
        Player driver = getDriver();
        if (driver == null || !driver.isAlive()) park();
        boolean stale = lastInput == Long.MIN_VALUE || level().getGameTime() - lastInput > 10;
        int active = stale ? VehicleNetwork.BRAKE : buttons;
        int throttle = ((active & VehicleNetwork.FORWARD) != 0 ? 1 : 0)
                - ((active & VehicleNetwork.REVERSE) != 0 ? 1 : 0);
        int steering = ((active & VehicleNetwork.RIGHT) != 0 ? 1 : 0)
                - ((active & VehicleNetwork.LEFT) != 0 ? 1 : 0);
        steeringInput=steering;
        boolean grounded = onGround() || isNoGravity();
        boolean braking = driver != null && ((active & VehicleNetwork.BRAKE) != 0
                || throttle * getDriveSpeed() < 0);
        entityData.set(BRAKING, braking);
        float speed = systems.speed(getDriveSpeed(), grounded && engineRunning() ? throttle : 0,
                !engineRunning() || isParkingBraked() || (active & VehicleNetwork.BRAKE) != 0,stale);
        if(systems.doors()!=0||systems.upgrades.hood()||systems.upgrades.jacked()){speed=0;systems.cancelCruise();}
        if (grounded && Math.abs(speed) > 0.001F && steering != 0) {
            float oldYaw = getYRot();
            setYRot(oldYaw + steering * Math.signum(speed) * (spec()==VehicleSpec.BUS?1.8F:spec()==VehicleSpec.MOTORCYCLE?4.2F:3F)
                    * Math.min(1F, Math.abs(speed) / 0.18F) * Math.max(.35F,systems.grip())*(systems.upgrades.weakest()<15?.55F:1F));
            if (!level().noCollision(this, getBoundingBox().deflate(0.001))) setYRot(oldYaw);
        }
        double yaw = Math.toRadians(getYRot());
        double vertical = isNoGravity() ? 0 : getDeltaMovement().y - 0.08;
        setDeltaMovement(-Math.sin(yaw) * speed, vertical, Math.cos(yaw) * speed);
        double previousX=getX(),previousZ=getZ();float impact=Math.abs(speed);
        move(MoverType.SELF, getDeltaMovement());
        systems.travelled(Math.hypot(getX()-previousX,getZ()-previousZ),horizontalCollision?impact:0);
        if (horizontalCollision) {speed = 0;systems.cancelCruise();}
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
        if(held.is(VehicleContent.RACK.get())||held.is(VehicleContent.JACK.get())||held.is(VehicleContent.SPARE_WHEEL.get())){
            if(!level().isClientSide)return systems.upgrades.tool(player,held)?InteractionResult.CONSUME:InteractionResult.FAIL;return InteractionResult.SUCCESS;
        }
        if(held.is(VehicleContent.ROPE.get())||held.is(VehicleContent.JUMPER.get())||held.is(VehicleContent.REMOTE.get())){
            if(!level().isClientSide)return VehicleRecovery.useTool(this,player,held)?InteractionResult.CONSUME:InteractionResult.FAIL;
            return InteractionResult.SUCCESS;
        }
        if(held.is(VehicleContent.PAPERS.get())){
            if(!level().isClientSide){equipment.claim(player);return VehicleManagement.open(this,player)?InteractionResult.CONSUME:InteractionResult.FAIL;}
            return InteractionResult.SUCCESS;
        }
        if(held.is(VehicleContent.FUEL_CAN.get())){
            if(!level().isClientSide)return FuelCanItem.refuel(this,player,held)?InteractionResult.CONSUME:InteractionResult.FAIL;
            return InteractionResult.SUCCESS;
        }
        if(held.is(VehicleContent.REPAIR_TOOL.get())){
            if(!level().isClientSide)return systems.repair(player,held)?InteractionResult.CONSUME:InteractionResult.FAIL;
            return InteractionResult.SUCCESS;
        }
        if (!level().isClientSide) equipment.claim(player);
        if (held.is(VehicleContent.KEY.get())) {
            if (!level().isClientSide) {
                if (equipment.bindKey(player, held)) player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.key_bound"), true);
                else if (equipment.matches(held) && equipment.authorized(player)) {
                    if (player.isShiftKeyDown()) {
                        if (!equipment.open(player)) player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.park_first"), true);
                    } else toggleLock(player);
                } else return InteractionResult.FAIL;
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (isLocked() && !equipment.authorized(player)) {
            if (!level().isClientSide) player.displayClientMessage(Component.translatable("message.ng_fahrzeuge.locked"), true);
            return InteractionResult.FAIL;
        }
        if (held.getItem() instanceof DyeItem dye) {
            if (!equipment.authorized(player)) return InteractionResult.FAIL;
            if (isVehicle() && getDriver() != player) return InteractionResult.FAIL;
            if (!level().isClientSide && color() != dye.getDyeColor().getId()) {
                entityData.set(COLOR, dye.getDyeColor().getId());
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty()) {
            if (isVehicle() || !equipment.isOwner(player) || equipment.hasOpeners() || systems.attached() || systems.comfort.connected()) return InteractionResult.FAIL;
            if (!level().isClientSide) {
                ItemStack returned = getPickResult();
                discard();
                if (!player.addItem(returned)) {
                    player.drop(returned, false);
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (player.isSecondaryUseActive()) return InteractionResult.PASS;
        if (!level().isClientSide) {
            if(spec()==VehicleSpec.MOTORCYCLE&&getPassengers().isEmpty())systems.expansion.raiseStand();
            if (!canAddPassenger(player) || !player.startRiding(this)) return InteractionResult.FAIL;
            player.displayClientMessage(Component.translatable(getDriver() == player
                    ? "message.ng_fahrzeuge.controls" : "message.ng_fahrzeuge.passenger"), true);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }
}
