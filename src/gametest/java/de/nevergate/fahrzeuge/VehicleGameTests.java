package de.nevergate.fahrzeuge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(value = NgFahrzeuge.MOD_ID, namespace = NgFahrzeuge.MOD_ID)
public final class VehicleGameTests {
    private static Player player(GameTestHelper h, boolean creative) {
        Player player = h.makeMockPlayer(); player.setUUID(java.util.UUID.randomUUID());
        player.getAbilities().mayBuild = true;
        player.getAbilities().instabuild = creative;
        player.setYRot(0);
        return player;
    }

    private static UseOnContext context(GameTestHelper h, Player player) {
        BlockPos ground = h.absolutePos(new BlockPos(1, 1, 1));
        return new UseOnContext(h.getLevel(), player, InteractionHand.MAIN_HAND,
                player.getMainHandItem(), new BlockHitResult(
                Vec3.atBottomCenterOf(ground.above()), Direction.UP, ground, false));
    }

    private static NgCityEntity spawn(GameTestHelper h) {
        NgCityEntity car = VehicleContent.NG_CITY.get().create(h.getLevel());
        Vec3 center = h.absoluteVec(new Vec3(1.5, 2, 1.5));
        car.setPos(center);
        h.getLevel().addFreshEntity(car);
        return car;
    }

    private static void ground(GameTestHelper h) {
        for (int x = 0; x < 3; x++) for (int z = 0; z < 3; z++) {
            h.setBlock(new BlockPos(x, 1, z), Blocks.STONE);
        }
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void placementConsumesExactlyOneAndRejectsOverlap(GameTestHelper h) {
        ground(h);
        Player player = player(h, false);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(VehicleContent.NG_CITY_ITEM.get()));
        h.assertTrue(VehicleContent.NG_CITY_ITEM.get().useOn(context(h, player)).consumesAction(),
                "Valid placement failed");
        h.assertTrue(player.getMainHandItem().isEmpty(), "Placement did not consume its item");
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(VehicleContent.NG_CITY_ITEM.get()));
        h.assertFalse(VehicleContent.NG_CITY_ITEM.get().useOn(context(h, player)).consumesAction(),
                "Overlapping placement succeeded");
        h.assertTrue(player.getMainHandItem().getCount() == 1, "Failed placement consumed an item");
        h.assertTrue(h.getLevel().getEntitiesOfClass(NgCityEntity.class,
                new net.minecraft.world.phys.AABB(h.absolutePos(new BlockPos(0, 1, 0))).inflate(4)).size() == 1,
                "Wrong vehicle count after duplicate placement");
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void obstructionAndBuildPermissionsRejectPlacement(GameTestHelper h) {
        ground(h);
        Player player = player(h, false);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(VehicleContent.NG_CITY_ITEM.get()));
        player.getAbilities().mayBuild = false;
        h.assertFalse(VehicleContent.NG_CITY_ITEM.get().useOn(context(h, player)).consumesAction(),
                "Placement bypassed build permissions");
        player.getAbilities().mayBuild = true;
        h.setBlock(new BlockPos(1, 2, 1), Blocks.STONE);
        h.assertFalse(VehicleContent.NG_CITY_ITEM.get().useOn(context(h, player)).consumesAction(),
                "Vehicle placed inside a solid block");
        h.assertTrue(player.getMainHandItem().getCount() == 1, "Rejected placement consumed item");
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void creativePlacementDoesNotConsumeItem(GameTestHelper h) {
        ground(h);
        Player player = player(h, true);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(VehicleContent.NG_CITY_ITEM.get()));
        h.assertTrue(VehicleContent.NG_CITY_ITEM.get().useOn(context(h, player)).consumesAction(),
                "Creative placement failed");
        h.assertTrue(player.getMainHandItem().getCount() == 1, "Creative placement consumed item");
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void reclaimRespectsPermissionsAndCannotDuplicate(GameTestHelper h) {
        NgCityEntity car = spawn(h);
        Player player = player(h, false);
        player.setShiftKeyDown(true);
        player.getAbilities().mayBuild = false;
        car.interact(player, InteractionHand.MAIN_HAND);
        h.assertFalse(car.isRemoved(), "Unauthorized pickup removed vehicle");
        player.getAbilities().mayBuild = true;
        car.interact(player, InteractionHand.MAIN_HAND);
        car.interact(player, InteractionHand.MAIN_HAND);
        h.assertTrue(car.isRemoved(), "Valid pickup left vehicle behind");
        h.assertTrue(player.getInventory().countItem(VehicleContent.NG_CITY_ITEM.get()) == 1,
                "Repeated pickup duplicated/lost vehicle item");
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 20)
    public static void saveRoundtripKeepsIdentityNameAndFootprint(GameTestHelper h) {
        NgCityEntity car = spawn(h);
        car.setYRot(90);
        car.setPos(car.position());
        car.setCustomName(Component.literal("NeverGate City 01"));
        CompoundTag tag = new CompoundTag();
        car.saveWithoutId(tag);
        NgCityEntity restored = VehicleContent.NG_CITY.get().create(h.getLevel());
        restored.load(tag);
        h.assertTrue(restored.getUUID().equals(car.getUUID()), "Saved vehicle identity changed");
        h.assertTrue(restored.position().distanceTo(car.position()) < 0.00001, "Saved position changed");
        h.assertTrue(restored.getYRot() == 90, "Saved rotation changed");
        h.assertTrue(restored.getCustomName().getString().equals("NeverGate City 01"), "Saved name changed");
        h.assertTrue(restored.getBoundingBox().getXsize() > restored.getBoundingBox().getZsize(),
                "Rotated footprint did not survive loading");
        h.assertTrue(restored.getPickResult().getHoverName().getString().equals("NeverGate City 01"),
                "Reclaimed item lost its custom name");
        h.succeed();
    }

    @GameTest(template = "forge:empty3x3x3", timeoutTicks = 60)
    public static void gravitySettlesVehicleWithoutHorizontalDrift(GameTestHelper h) {
        ground(h);
        NgCityEntity car = spawn(h);
        car.setPos(car.getX(), car.getY() + 2, car.getZ());
        double x = car.getX(), z = car.getZ();
        double floor = h.absolutePos(new BlockPos(1, 2, 1)).getY();
        h.runAfterDelay(35, () -> {
            h.assertTrue(car.onGround(), "Vehicle did not settle on ground");
            h.assertTrue(Math.abs(car.getY() - floor) < 0.01, "Vehicle sank into ground");
            h.assertTrue(car.getX() == x && car.getZ() == z, "Parked vehicle drifted");
            h.succeed();
        });
    }
}
