package de.nevergate.fahrzeuge;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;

@GameTestHolder(value=NgFahrzeuge.MOD_ID, namespace=NgFahrzeuge.MOD_ID)
public final class Dev7GameTests {
    private static NgCityEntity car(GameTestHelper h, VehicleSpec spec) {
        NgCityEntity car=VehicleContent.type(spec).create(h.getLevel());
        var at=h.absolutePos(new BlockPos(1,1,1));
        car.setPos(at.getX()+.5, h.getLevel().getMaxBuildHeight()-8,at.getZ()+.5);
        car.setNoGravity(true);h.getLevel().addFreshEntity(car);return car;
    }
    private static Player player(GameTestHelper h,NgCityEntity car) {
        Player p=h.makeMockPlayer();p.setUUID(UUID.randomUUID());p.getAbilities().mayBuild=true;
        p.getAbilities().instabuild=false;p.setPos(car.position());return p;
    }
    private static Player driver(GameTestHelper h,NgCityEntity car) {
        Player p=player(h,car);car.interact(p,InteractionHand.MAIN_HAND);return p;
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void engineOffBlocksThrottleAndStartEnablesDriving(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.CITY);Player p=driver(h,car);
        car.applyAction(p,VehicleNetwork.PARK_ACTION);car.acceptInput(p,VehicleNetwork.FORWARD);
        for(int i=0;i<6;i++)car.tick();
        h.assertTrue(car.getDriveSpeed()==0&&!car.engineRunning(),"Engine off allowed driving");
        h.assertTrue(car.applyAction(p,VehicleNetwork.ENGINE_ACTION),"Owner could not start motor");
        for(int i=0;i<6;i++)car.tick();
        h.assertTrue(car.getDriveSpeed()>0&&car.engineRunning(),"Started motor did not drive");
        p.stopRiding();h.assertTrue(!car.engineRunning()&&car.isParkingBraked(),"Exit left engine running");
        car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void engineShutdownBrakesAndLoadNeverRestartsMotor(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.CITY);Player p=driver(h,car);
        car.applyAction(p,VehicleNetwork.ENGINE_ACTION);car.applyAction(p,VehicleNetwork.PARK_ACTION);
        car.acceptInput(p,VehicleNetwork.FORWARD);for(int i=0;i<8;i++)car.tick();
        car.applyAction(p,VehicleNetwork.ENGINE_ACTION);for(int i=0;i<10;i++)car.tick();
        h.assertTrue(!car.engineRunning()&&car.getDriveSpeed()==0,"Shutdown did not brake");
        car.applyAction(p,VehicleNetwork.LIGHT_ACTION);car.applyAction(p,VehicleNetwork.ENGINE_ACTION);
        CompoundTag saved=new CompoundTag();car.saveWithoutId(saved);
        NgCityEntity restored=VehicleContent.NG_CITY.get().create(h.getLevel());restored.load(saved);
        h.assertFalse(restored.engineRunning(),"NBT restart restored an active engine");
        car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void indicatorsToggleIndependentlyHazardsReplaceThemAndPassengerIsRejected(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.FAMILY);Player p=driver(h,car),other=player(h,car);other.startRiding(car);
        h.assertFalse(car.applyAction(other,VehicleNetwork.LEFT_ACTION),"Passenger changed indicator");
        car.applyAction(p,VehicleNetwork.LEFT_ACTION);h.assertTrue(car.signal()==1,"Left missing");
        car.applyAction(p,VehicleNetwork.RIGHT_ACTION);h.assertTrue(car.signal()==2,"Right did not replace left");
        car.applyAction(p,VehicleNetwork.HAZARD_ACTION);h.assertTrue(car.signal()==3,"Hazards missing");
        h.assertTrue(car.indicatorLit(true)==car.indicatorLit(false),"Hazards flash at different phases");
        car.applyAction(p,VehicleNetwork.HAZARD_ACTION);h.assertTrue(car.signal()==0,"Hazards did not toggle off");
        h.assertTrue(car.applyAction(p,VehicleNetwork.HORN_ACTION),"Horn rejected");
        h.assertFalse(car.applyAction(p,VehicleNetwork.HORN_ACTION),"Horn rate limit bypassed");
        car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void ownerCanBindAdditionalKeysAndSharedKeyAllowsLockedEntry(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.FAMILY);Player owner=player(h,car),guest=player(h,car);
        car.equipment.claim(owner);
        ItemStack key=new ItemStack(VehicleContent.KEY.get()),second=new ItemStack(VehicleContent.KEY.get());
        h.assertFalse(car.equipment.bindKey(guest,key),"Stranger bound owner's car");
        h.assertTrue(car.equipment.bindKey(owner,key)&&car.equipment.bindKey(owner,second),"Owner cannot create additional keys");
        h.assertTrue(car.toggleLock(owner)&&car.isLocked(),"Owner cannot lock");
        h.assertFalse(car.interact(guest,InteractionHand.MAIN_HAND).consumesAction(),"Locked vehicle allowed stranger");
        guest.getInventory().setItem(8,second);h.assertTrue(car.interact(guest,InteractionHand.MAIN_HAND).consumesAction(),"Shared key did not allow entry");
        h.assertTrue(car.applyAction(guest,VehicleNetwork.ENGINE_ACTION),"Shared key did not allow driving");
        car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void unlockedGuestCannotStartEnginePaintReclaimOrOpenCargoWithoutKey(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.CITY);Player owner=player(h,car),guest=player(h,car);
        car.equipment.claim(owner);guest.startRiding(car);
        h.assertFalse(car.applyAction(guest,VehicleNetwork.ENGINE_ACTION),"Unkeyed guest started engine");
        h.assertFalse(car.equipment.authorized(guest),"Unkeyed guest authorized");guest.stopRiding();
        guest.setShiftKeyDown(true);car.interact(guest,InteractionHand.MAIN_HAND);
        h.assertFalse(car.isRemoved(),"Unkeyed guest reclaimed car");
        h.assertFalse(car.equipment.canOpen(guest),"Unkeyed guest opened cargo");
        guest.setShiftKeyDown(false);guest.setItemInHand(InteractionHand.MAIN_HAND,new ItemStack(Items.RED_DYE));
        h.assertFalse(car.interact(guest,InteractionHand.MAIN_HAND).consumesAction(),"Unkeyed guest painted car");
        car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void cargoSlotsOwnershipKeysAndLockSurviveNbtAndPacking(GameTestHelper h) {
        for(VehicleSpec spec:VehicleSpec.values()) {
            NgCityEntity car=car(h,spec);Player owner=player(h,car);car.equipment.claim(owner);
            ItemStack key=new ItemStack(VehicleContent.KEY.get());car.equipment.bindKey(owner,key);
            int end=car.equipment.cargo.getContainerSize()-1;
            car.equipment.cargo.setItem(end,new ItemStack(Items.DIAMOND,7));car.toggleLock(owner);
            CompoundTag saved=new CompoundTag();car.saveWithoutId(saved);
            NgCityEntity loaded=VehicleContent.type(spec).create(h.getLevel());loaded.load(saved);
            h.assertTrue(loaded.equipment.owner().equals(owner.getUUID())&&loaded.equipment.matches(key)&&loaded.isLocked(),"Identity/lock not restored");
            h.assertTrue(loaded.equipment.cargo.getItem(0).isEmpty()&&loaded.equipment.cargo.getItem(end).getCount()==7,"Cargo slots changed during save");
            NgCityEntity unpacked=VehicleContent.type(spec).create(h.getLevel());unpacked.loadEquipment(car.getPickResult().getTag());
            h.assertTrue(unpacked.equipment.matches(key)&&unpacked.equipment.cargo.getItem(end).is(Items.DIAMOND),"Packing lost key or cargo");
            car.discard();
        }h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void cargoRequiresStationaryParkedEngineOffAndNearbyAuthorizedPlayer(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.TRANSPORTER);Player p=driver(h,car);
        h.assertTrue(car.equipment.cargo.getContainerSize()==54&&car.equipment.canOpen(p),"Van capacity/open failed");
        car.applyAction(p,VehicleNetwork.ENGINE_ACTION);h.assertFalse(car.equipment.canOpen(p),"Running engine allowed cargo");
        p.stopRiding();p.setPos(car.position());h.assertTrue(car.equipment.canOpen(p),"Parked cargo rejected");
        p.setPos(car.getX()+12,car.getY(),car.getZ());h.assertFalse(car.equipment.canOpen(p),"Remote cargo accepted");
        p.setPos(car.position());car.discard();h.assertFalse(car.equipment.canOpen(p),"Removed car cargo accepted");h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void openCargoPreventsReclaimAndPackingTransfersContentsOnce(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.CITY);Player p=player(h,car);car.equipment.claim(p);
        car.equipment.cargo.setItem(4,new ItemStack(Items.EMERALD,3));p.setShiftKeyDown(true);
        car.equipment.cargo.startOpen(p);car.interact(p,InteractionHand.MAIN_HAND);
        h.assertFalse(car.isRemoved(),"Open cargo car reclaimed");car.equipment.cargo.stopOpen(p);
        car.interact(p,InteractionHand.MAIN_HAND);car.interact(p,InteractionHand.MAIN_HAND);
        h.assertTrue(car.isRemoved()&&p.getInventory().countItem(VehicleContent.NG_CITY_ITEM.get())==1,"Pickup lost/duplicated car");
        ItemStack packed=p.getInventory().items.stream().filter(s->s.is(VehicleContent.NG_CITY_ITEM.get())).findFirst().orElseThrow();
        NgCityEntity loaded=VehicleContent.NG_CITY.get().create(h.getLevel());loaded.loadEquipment(packed.getTag());
        h.assertTrue(loaded.equipment.cargo.getItem(4).getCount()==3,"Packed cargo lost");h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void assemblyChecksAllCostsBeforeConsumingAndBuildsCorrectGroups(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.CITY);Player p=player(h,car);
        p.getInventory().add(new ItemStack(Items.IRON_INGOT,64));
        h.assertFalse(VehicleAssembly.assemble(p,VehicleSpec.CITY),"Incomplete recipe succeeded");
        h.assertTrue(p.getInventory().countItem(Items.IRON_INGOT)==64,"Rejected recipe consumed material");
        p.getInventory().clearContent();
        for(VehicleSpec spec:VehicleSpec.values()) {
            for(var cost:VehicleAssembly.costs(spec))p.getInventory().add(new ItemStack(cost.item(),cost.count()));
            h.assertTrue(VehicleAssembly.assemble(p,spec),"Complete recipe rejected");
            h.assertTrue(p.getInventory().countItem(VehicleContent.item(spec))==1,"Wrong vehicle output");
            for(var cost:VehicleAssembly.costs(spec))h.assertTrue(p.getInventory().countItem(cost.item())==0,"Recipe did not consume exact material");
        }
        car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void workbenchRejectsRemovedBlockRemotePlayerAndInvalidSelection(GameTestHelper h) {
        NgCityEntity car=car(h,VehicleSpec.CITY);Player p=player(h,car);BlockPos pos=car.blockPosition().below();
        h.getLevel().setBlockAndUpdate(pos,VehicleContent.WORKBENCH.get().defaultBlockState());
        VehicleWorkbenchMenu menu=new VehicleWorkbenchMenu(1,p.getInventory(),pos);
        h.assertTrue(menu.stillValid(p),"Nearby workbench invalid");
        h.assertFalse(menu.clickMenuButton(p,999),"Invalid selection accepted");
        p.setPos(p.getX()+20,p.getY(),p.getZ());h.assertFalse(menu.clickMenuButton(p,0),"Remote assembly accepted");
        p.setPos(car.position());h.getLevel().removeBlock(pos,false);h.assertFalse(menu.stillValid(p),"Removed workbench still valid");
        car.discard();h.succeed();
    }
    @GameTest(template="forge:empty3x3x3",timeoutTicks=30)
    public static void craftingRecipesAndSoundsAreRegistered(GameTestHelper h) {
        var recipes=h.getLevel().getRecipeManager();
        h.assertTrue(recipes.byKey(new net.minecraft.resources.ResourceLocation("ng_fahrzeuge","vehicle_key")).isPresent(),"Key recipe missing");
        h.assertTrue(recipes.byKey(new net.minecraft.resources.ResourceLocation("ng_fahrzeuge","vehicle_workbench")).isPresent(),"Workbench recipe missing");
        h.assertTrue(VehicleSounds.SOUNDS.getEntries().size()==9,"Incomplete sound registration");h.succeed();
    }

}
