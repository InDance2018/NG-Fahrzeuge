package de.nevergate.fahrzeuge;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(value = NgFahrzeuge.MOD_ID, namespace = NgFahrzeuge.MOD_ID)
public final class DrivingGameTests {
    private static NgCityEntity car(GameTestHelper h) {
        NgCityEntity car = VehicleContent.NG_CITY.get().create(h.getLevel());
        car.setNoGravity(true);
        // The 3x3 template clears only a small part of the generated terrain.
        // Drive above that terrain; the explicit wall/ground tests build their own fixtures.
        Vec3 origin = h.absoluteVec(new Vec3(1.5, 0, 1.5));
        car.setPos(origin.x, h.getLevel().getMaxBuildHeight() - 8, origin.z);
        h.getLevel().addFreshEntity(car);
        return car;
    }

    private static Player player(GameTestHelper h) {
        Player player = h.makeMockPlayer();
        player.getAbilities().mayBuild = true;
        player.getAbilities().instabuild = false;
        return player;
    }

    private static Player enter(GameTestHelper h, NgCityEntity car) {
        Player driver = player(h);
        h.assertTrue(car.interact(driver, InteractionHand.MAIN_HAND).consumesAction(), "Entry failed");
        h.assertTrue(car.getDriver() == driver && driver.getVehicle() == car, "Driver seat missing");
        if (car.isParkingBraked()) car.applyAction(driver, VehicleNetwork.PARK_ACTION);
        return driver;
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void oneDriverOnlyAndOccupiedVehicleCannotBeReclaimed(GameTestHelper h) {
        NgCityEntity car = car(h);
        Player driver = enter(h, car);
        Player other = player(h);
        h.assertFalse(car.interact(other, InteractionHand.MAIN_HAND).consumesAction(), "Second driver entered");
        other.setShiftKeyDown(true);
        car.interact(other, InteractionHand.MAIN_HAND);
        h.assertFalse(car.isRemoved(), "Occupied car was reclaimed");
        h.assertTrue(car.getDriver() == driver && car.getPassengers().size() == 1, "Seat was stolen");
        h.assertTrue(car.getControllingPassenger() == null, "Vanilla client movement authority enabled");
        car.discard();
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void controlPacketsRequireCurrentDriverAndValidButtons(GameTestHelper h) {
        NgCityEntity car = car(h);
        Player driver = enter(h, car), other = player(h);
        h.assertFalse(VehicleNetwork.apply(other, new VehicleNetwork.DriveInput(car.getId(), 1)),
                "Unseated player controlled car");
        h.assertFalse(VehicleNetwork.apply(driver, new VehicleNetwork.DriveInput(car.getId() + 1, 1)),
                "Wrong vehicle ID accepted");
        h.assertFalse(VehicleNetwork.apply(driver, new VehicleNetwork.DriveInput(car.getId(), 255)),
                "Invalid buttons accepted");
        h.assertTrue(VehicleNetwork.apply(driver, new VehicleNetwork.DriveInput(car.getId(), 1)),
                "Valid driver input rejected");
        driver.stopRiding();
        h.assertFalse(VehicleNetwork.apply(driver, new VehicleNetwork.DriveInput(car.getId(), 1)),
                "Former driver controlled car after exiting");
        car.discard();
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void speedLimitsAndDirectionChangeAreBounded(GameTestHelper h) {
        float speed = 0;
        for (int i = 0; i < 200; i++) speed = NgCityEntity.nextSpeed(speed, 1, false);
        h.assertTrue(speed == NgCityEntity.MAX_FORWARD, "Forward speed limit failed");
        h.assertTrue(NgCityEntity.nextSpeed(speed, -1, false) > 0, "Reverse instantly changed direction");
        h.assertTrue(NgCityEntity.nextSpeed(0.01F, -1, false) == 0, "Reverse braking crossed zero");
        for (int i = 0; i < 200; i++) speed = NgCityEntity.nextSpeed(speed, -1, false);
        h.assertTrue(speed == -NgCityEntity.MAX_REVERSE, "Reverse speed limit failed");
        float coast = NgCityEntity.nextSpeed(0.2F, 0, false);
        float brake = NgCityEntity.nextSpeed(0.2F, 1, true);
        h.assertTrue(brake < coast, "Brake did not override accelerator");
        for (int i = 0; i < 20; i++) speed = NgCityEntity.nextSpeed(speed, 1, true);
        h.assertTrue(speed == 0, "Brake did not stop reverse travel");
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void serverMovesForwardReverseAndSteersWithoutTurningInPlace(GameTestHelper h) {
        NgCityEntity car = car(h);
        Player driver = enter(h, car);
        Vec3 start = car.position();
        car.acceptInput(driver, VehicleNetwork.RIGHT);
        car.tick();
        h.assertTrue(car.getYRot() == 0, "Stationary vehicle rotated");
        h.assertTrue(car.acceptInput(driver, VehicleNetwork.FORWARD), "Forward input rejected");
        for (int i = 0; i < 8; i++) car.tick();
        h.assertTrue(car.getZ() > start.z && car.getDriveSpeed() > 0, "Forward movement missing: speed="
                + car.getDriveSpeed() + " collision=" + car.horizontalCollision + " driver=" + (car.getDriver() == driver)
                + " start=" + start + " now=" + car.position() + " nearby="
                + car.level().getEntities(car, car.getBoundingBox().inflate(0.5)).stream()
                        .map(e -> e.getType().toString()).toList()
                + " frontBlock=" + car.level().getBlockState(BlockPos.containing(car.getX(), car.getY(),
                        car.getBoundingBox().maxZ + 0.02)));
        car.acceptInput(driver, VehicleNetwork.FORWARD | VehicleNetwork.RIGHT);
        for (int i = 0; i < 5; i++) car.tick();
        h.assertTrue(car.getYRot() > 0 && car.getX() < start.x, "Right steering failed");
        h.assertTrue(car.acceptInput(driver, VehicleNetwork.REVERSE), "Reverse input rejected");
        for (int i = 0; i < 40; i++) car.tick();
        h.assertTrue(car.getDriveSpeed() < 0, "Reverse movement missing: speed=" + car.getDriveSpeed()
                + " collision=" + car.horizontalCollision + " driver=" + (car.getDriver() == driver)
                + " noGravity=" + car.isNoGravity() + " position=" + car.position()
                + " nearby=" + car.level().getEntities(car, car.getBoundingBox().inflate(0.5))
                        .stream().map(e -> e.getType().toString()).toList());
        car.discard();
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void brakeStopsActualVehicleAndHoldsIt(GameTestHelper h) {
        NgCityEntity car = car(h);
        Player driver = enter(h, car);
        car.acceptInput(driver, VehicleNetwork.FORWARD);
        for (int i = 0; i < 20; i++) car.tick();
        car.acceptInput(driver, VehicleNetwork.FORWARD | VehicleNetwork.BRAKE);
        for (int i = 0; i < 12; i++) car.tick();
        Vec3 stopped = car.position();
        for (int i = 0; i < 5; i++) car.tick();
        h.assertTrue(car.getDriveSpeed() == 0 && car.position().equals(stopped), "Braked car kept moving");
        car.discard();
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void solidWallStopsDriving(GameTestHelper h) {
        NgCityEntity car = car(h);
        Player driver = enter(h, car);
        BlockPos base = BlockPos.containing(car.getX(), car.getY(), car.getZ() + 3);
        for (int x = -2; x <= 2; x++) for (int y = 0; y < 4; y++) {
            h.getLevel().setBlockAndUpdate(base.offset(x, y, 0), Blocks.STONE.defaultBlockState());
        }
        car.acceptInput(driver, VehicleNetwork.FORWARD);
        for (int i = 0; i < 40; i++) car.tick();
        h.assertTrue(car.getBoundingBox().maxZ <= base.getZ() + 0.001, "Car drove through wall");
        h.assertTrue(car.getDriveSpeed() == 0, "Wall collision did not stop drive speed");
        car.discard();
        for (int x = -2; x <= 2; x++) for (int y = 0; y < 4; y++) {
            h.getLevel().removeBlock(base.offset(x, y, 0), false);
        }
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 60)
    public static void missingInputTimesOutAndBrakes(GameTestHelper h) {
        NgCityEntity car = car(h);
        car.setPos(car.getX(), car.getY() + 20, car.getZ());
        Player driver = enter(h, car);
        car.acceptInput(driver, VehicleNetwork.FORWARD);
        h.runAfterDelay(5, () -> h.assertTrue(car.getDriveSpeed() > 0,
                "Timeout fixture did not begin driving"));
        h.runAfterDelay(30, () -> {
            h.assertTrue(car.getDriveSpeed() == 0, "Missing input did not stop vehicle");
            car.discard();
            h.succeed();
        });
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void exitingParksAndReentryDoesNotReuseThrottle(GameTestHelper h) {
        NgCityEntity car = car(h);
        Player driver = enter(h, car);
        car.acceptInput(driver, VehicleNetwork.FORWARD);
        for (int i = 0; i < 8; i++) car.tick();
        h.assertTrue(car.getDriveSpeed() > 0, "Setup vehicle never moved: collision=" + car.horizontalCollision
                + " position=" + car.position() + " nearby=" + car.level()
                .getEntities(car, car.getBoundingBox().inflate(0.5)).stream().map(e -> e.getType().toString()).toList());
        driver.stopRiding();
        h.assertTrue(car.getDriveSpeed() == 0 && car.getDeltaMovement().equals(Vec3.ZERO),
                "Dismount did not park vehicle");
        Vec3 stopped = car.position();
        enter(h, car);
        for (int i = 0; i < 5; i++) car.tick();
        h.assertTrue(car.position().equals(stopped), "New driver inherited previous throttle");
        car.discard();
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void loadingMovingVehicleResetsMotionAndControls(GameTestHelper h) {
        NgCityEntity car = car(h);
        Player driver = enter(h, car);
        car.acceptInput(driver, VehicleNetwork.FORWARD);
        for (int i = 0; i < 8; i++) car.tick();
        car.setDeltaMovement(0.4, 0, 0.4);
        CompoundTag saved = new CompoundTag();
        car.saveWithoutId(saved);
        NgCityEntity restored = VehicleContent.NG_CITY.get().create(h.getLevel());
        restored.load(saved);
        h.assertTrue(restored.getDriveSpeed() == 0 && restored.getDeltaMovement().equals(Vec3.ZERO),
                "Loaded vehicle retained motion");
        h.assertTrue(restored.getDriver() == null, "Loaded test vehicle retained a phantom driver");
        car.discard();
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void dismountSearchUsesFreeSideWhenOtherSideIsBlocked(GameTestHelper h) {
        for (int x = -2; x <= 5; x++) for (int z = -2; z <= 5; z++) {
            h.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
        }
        NgCityEntity car = car(h);
        car.setPos(h.absoluteVec(new Vec3(1.5, 2, 1.5)));
        Player driver = enter(h, car);
        for (int z = 0; z < 3; z++) for (int y = 2; y < 5; y++) {
            h.setBlock(new BlockPos(3, y, z), Blocks.STONE);
        }
        Vec3 exit = car.getDismountLocationForPassenger(driver);
        h.assertTrue(exit.x < car.getX(), "Blocked side chosen for dismount");
        h.assertTrue(!driver.getDimensions(Pose.STANDING).makeBoundingBox(exit)
                .intersects(car.getBoundingBox()), "Exit remained inside vehicle");
        car.discard();
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void drivingPacketSerializationKeepsButtonsAndEntityId(GameTestHelper h) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            VehicleNetwork.DriveInput input = new VehicleNetwork.DriveInput(734,
                    VehicleNetwork.REVERSE | VehicleNetwork.LEFT | VehicleNetwork.BRAKE);
            VehicleNetwork.encode(input, buffer);
            h.assertTrue(VehicleNetwork.decode(buffer).equals(input), "Drive packet changed in transit");
            h.assertTrue(buffer.readableBytes() == 0, "Drive packet left unread data");
        } finally {
            buffer.release();
        }
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void drivesOnGroundAndClimbsHalfBlockStep(GameTestHelper h) {
        NgCityEntity car = car(h);
        car.setNoGravity(false);
        BlockPos base = car.blockPosition();
        for (int x = -3; x <= 3; x++) for (int z = -4; z <= 16; z++) {
            h.getLevel().setBlockAndUpdate(base.offset(x, -1, z), Blocks.STONE.defaultBlockState());
        }
        for (int x = -2; x <= 2; x++) {
            h.getLevel().setBlockAndUpdate(base.offset(x, 0, 4), Blocks.STONE_SLAB.defaultBlockState());
        }
        Player driver = enter(h, car);
        car.acceptInput(driver, VehicleNetwork.FORWARD);
        double start = car.getZ(), highest = car.getY();
        for (int i = 0; i < 30; i++) {
            car.tick();
            highest = Math.max(highest, car.getY());
        }
        h.assertTrue(car.getZ() > start + 2, "Vehicle did not drive on real ground");
        h.assertTrue(highest >= base.getY() + 0.49, "Vehicle could not climb half-block step");
        car.discard();
        for (int x = -3; x <= 3; x++) for (int z = -4; z <= 16; z++) {
            h.getLevel().removeBlock(base.offset(x, -1, z), false);
        }
        for (int x = -2; x <= 2; x++) h.getLevel().removeBlock(base.offset(x, 0, 4), false);
        h.succeed();
    }
}
