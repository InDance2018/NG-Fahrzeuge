package de.nevergate.fahrzeuge.client;

import de.nevergate.fahrzeuge.NgFahrzeuge;
import de.nevergate.fahrzeuge.VehicleContent;
import de.nevergate.fahrzeuge.VehicleSpec;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NgFahrzeuge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT)
public final class VehicleClient {
    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (VehicleSpec spec : VehicleSpec.values()) {
            event.registerLayerDefinition(NgCityModel.layer(spec), () -> NgCityModel.createLayer(spec));
        }
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        for (VehicleSpec spec : VehicleSpec.values()) {
            event.registerEntityRenderer(VehicleContent.type(spec), context -> new NgCityRenderer(context, spec));
        }
    }

    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event) {
        event.register(DrivingClient.PARK); event.register(DrivingClient.LIGHTS);
    }

    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event) {
        for (VehicleSpec spec : VehicleSpec.values()) event.register((stack, tint) -> {
            int id = stack.hasTag() && stack.getTag().contains("VehicleColor", 3)
                    ? stack.getTag().getInt("VehicleColor") : spec.defaultColor.getId();
            if (id < 0 || id > 15) id = spec.defaultColor.getId();
            float[] rgb = DyeColor.byId(id).getTextureDiffuseColors();
            return (Math.round(rgb[0]*255) << 16) | (Math.round(rgb[1]*255) << 8) | Math.round(rgb[2]*255);
        }, VehicleContent.item(spec));
    }

    private VehicleClient() {}
}
