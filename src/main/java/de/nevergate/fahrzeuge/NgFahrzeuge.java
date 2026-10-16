package de.nevergate.fahrzeuge;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/** Independent mod entry point. Vehicle content is added in the next Dev1 step. */
@Mod(NgFahrzeuge.MOD_ID)
public final class NgFahrzeuge {
    public static final String MOD_ID = "ng_fahrzeuge";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NgFahrzeuge() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("NG-Fahrzeuge: independent mod foundation initialized; vehicles pending.");
    }
}
