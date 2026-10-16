package de.nevergate.fahrzeuge.client;

import de.nevergate.fahrzeuge.NgCityEntity;
import de.nevergate.fahrzeuge.NgFahrzeuge;
import de.nevergate.fahrzeuge.VehicleNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = NgFahrzeuge.MOD_ID, value = Dist.CLIENT)
public final class DrivingClient {
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null
                || !(mc.player.getVehicle() instanceof NgCityEntity car)
                || car.getDriver() != mc.player) return;
        int buttons = 0;
        if (mc.screen != null || !mc.isWindowActive() || mc.options.keyShift.isDown()) {
            buttons = VehicleNetwork.BRAKE;
        } else {
            if (mc.options.keyUp.isDown()) buttons |= VehicleNetwork.FORWARD;
            if (mc.options.keyDown.isDown()) buttons |= VehicleNetwork.REVERSE;
            if (mc.options.keyLeft.isDown()) buttons |= VehicleNetwork.LEFT;
            if (mc.options.keyRight.isDown()) buttons |= VehicleNetwork.RIGHT;
            if (mc.options.keyJump.isDown()) buttons |= VehicleNetwork.BRAKE;
        }
        VehicleNetwork.CHANNEL.send(new VehicleNetwork.DriveInput(car.getId(), buttons),
                PacketDistributor.SERVER.noArg());
        if (mc.screen == null && mc.player.tickCount % 10 == 0) {
            mc.player.displayClientMessage(Component.translatable("hud.ng_fahrzeuge.speed",
                    Math.round(Math.abs(car.getDriveSpeed()) * 72)), true);
        }
    }

    private DrivingClient() {}
}
