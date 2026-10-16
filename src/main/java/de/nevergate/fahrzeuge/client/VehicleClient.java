package de.nevergate.fahrzeuge.client;

import de.nevergate.fahrzeuge.NgFahrzeuge;
import de.nevergate.fahrzeuge.VehicleContent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NgFahrzeuge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT)
public final class VehicleClient {
    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(NgCityModel.LAYER, NgCityModel::createLayer);
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VehicleContent.NG_CITY.get(), NgCityRenderer::new);
    }

    private VehicleClient() {}
}
