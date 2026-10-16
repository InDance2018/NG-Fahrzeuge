package de.nevergate.fahrzeuge;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/** Common mod entry point; deliberately contains no client class references. */
@Mod(NgFahrzeuge.MOD_ID)
public final class NgFahrzeuge {
    public static final String MOD_ID = "ng_fahrzeuge";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NgFahrzeuge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        VehicleContent.ENTITIES.register(modBus);
        VehicleContent.ITEMS.register(modBus);
        VehicleContent.TABS.register(modBus);
        VehicleContent.BLOCKS.register(modBus);
        VehicleContent.MENUS.register(modBus);
        VehicleSounds.SOUNDS.register(modBus);
        VehicleNetwork.register();
        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("NG-Fahrzeuge: NG City driving test initialized.");
    }
}
