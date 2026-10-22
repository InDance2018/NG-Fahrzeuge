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
    @SubscribeEvent public static void setup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> net.minecraft.client.gui.screens.MenuScreens.register(VehicleContent.SEMI_MENU.get(),TrailerCargoScreen::new));
        event.enqueueWork(() -> net.minecraft.client.gui.screens.MenuScreens.register(VehicleContent.DIAGNOSIS_MENU.get(),VehicleDiagnosisScreen::new));
        event.enqueueWork(() -> net.minecraft.client.gui.screens.MenuScreens.register(VehicleContent.DIRECTORY_MENU.get(), VehicleDirectoryScreen::new));
        event.enqueueWork(() -> net.minecraft.client.gui.screens.MenuScreens.register(VehicleContent.WORKBENCH_MENU.get(), VehicleWorkbenchScreen::new));
        event.enqueueWork(() -> net.minecraft.client.gui.screens.MenuScreens.register(VehicleContent.MANAGEMENT_MENU.get(), VehicleManagementScreen::new));
        event.enqueueWork(() -> net.minecraft.client.gui.screens.MenuScreens.register(VehicleContent.SERVICE_MENU.get(), VehicleServiceScreen::new));
    }
    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (VehicleSpec spec : VehicleSpec.values()) {
            event.registerLayerDefinition(NgCityModel.layer(spec), () -> NgCityModel.createLayer(spec));
        }
    }

    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VehicleContent.TRAILER.get(),TrailerRenderer::new);
        for (VehicleSpec spec : VehicleSpec.values()) {
            event.registerEntityRenderer(VehicleContent.type(spec), context -> new NgCityRenderer(context, spec));
        }
    }

    @SubscribeEvent public static void keys(RegisterKeyMappingsEvent event) {
        for (var key : DrivingClient.ACTION_KEYS) event.register(key);event.register(DrivingClient.CAMERA);
        event.register(DrivingClient.HUD_DETAILS);event.register(DrivingClient.HUD_SMALLER);event.register(DrivingClient.HUD_LARGER);
    }

    @SubscribeEvent public static void colors(RegisterColorHandlersEvent.Item event) {
        for (VehicleSpec spec : VehicleSpec.values()) event.register((stack, tint) -> {
            int id = stack.hasTag() && stack.getTag().contains("VehicleColor", 3)
                    ? stack.getTag().getInt("VehicleColor") : spec.defaultColor.getId();
            if (id < 0 || id > 15) id = spec.defaultColor.getId();
            float[] rgb = VehicleTextures.paint(id);
            return (Math.round(rgb[0]*255) << 16) | (Math.round(rgb[1]*255) << 8) | Math.round(rgb[2]*255);
        }, spec==VehicleSpec.FAMILY?new net.minecraft.world.level.ItemLike[]{VehicleContent.item(spec),VehicleContent.FAMILY_ROOF.get()}:spec==VehicleSpec.TRANSPORTER?new net.minecraft.world.level.ItemLike[]{VehicleContent.item(spec),VehicleContent.VAN_WINDOWS.get()}:new net.minecraft.world.level.ItemLike[]{VehicleContent.item(spec)});
    }

    private VehicleClient() {}
}
