package de.nevergate.fahrzeuge.client;

import de.nevergate.fahrzeuge.NgCityEntity;
import de.nevergate.fahrzeuge.NgFahrzeuge;
import de.nevergate.fahrzeuge.VehicleNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = NgFahrzeuge.MOD_ID, value = Dist.CLIENT)
public final class DrivingClient {
    private static KeyMapping key(String name, int code) {
        return new KeyMapping("key.ng_fahrzeuge." + name, InputConstants.Type.KEYSYM, code, "key.categories.ng_fahrzeuge");
    }
    public static final KeyMapping PARK = key("parking", GLFW.GLFW_KEY_H), LIGHTS = key("lights", GLFW.GLFW_KEY_L),
            ENGINE = key("engine", GLFW.GLFW_KEY_I), LEFT = key("left", GLFW.GLFW_KEY_COMMA),
            RIGHT = key("right", GLFW.GLFW_KEY_PERIOD), HAZARD = key("hazard", GLFW.GLFW_KEY_R),
            HORN = key("horn", GLFW.GLFW_KEY_G), CARGO = key("cargo", GLFW.GLFW_KEY_B),
            DOORS=key("doors",GLFW.GLFW_KEY_O),HATCH=key("hatch",GLFW.GLFW_KEY_U),
            TRIP=key("trip",GLFW.GLFW_KEY_UNKNOWN),CRUISE=key("cruise",GLFW.GLFW_KEY_C),COUPLE=key("couple",GLFW.GLFW_KEY_J),WIPERS=key("wipers",GLFW.GLFW_KEY_K),WINDOWS=key("windows",GLFW.GLFW_KEY_V),CABIN=key("cabin",GLFW.GLFW_KEY_N),HOOD=key("hood",GLFW.GLFW_KEY_M),BEACON=key("beacon",GLFW.GLFW_KEY_Y),ROOF=key("roof",GLFW.GLFW_KEY_Z);
    public static final KeyMapping STOP=key("stop_request",GLFW.GLFW_KEY_F9),BELT=key("seatbelt",GLFW.GLFW_KEY_F7),HIGH=key("high_beam",GLFW.GLFW_KEY_F10),STAND=key("side_stand",GLFW.GLFW_KEY_F8),FOG=key("fog",GLFW.GLFW_KEY_F12);
    public static final KeyMapping CAMERA=key("camera",GLFW.GLFW_KEY_F6);
    public static final KeyMapping[] ACTION_KEYS = {PARK, LIGHTS, ENGINE, LEFT, RIGHT, HAZARD, HORN, CARGO, DOORS, HATCH, TRIP, CRUISE, COUPLE,WIPERS,WINDOWS,CABIN,HOOD,BEACON,ROOF,STOP,BELT,HIGH,STAND,FOG};
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        VehicleAudio.tick(mc);VehicleCamera.tick(mc);
        while(CAMERA.consumeClick())if(mc.screen==null)VehicleCamera.toggle(mc);
        if (mc.player == null || mc.getConnection() == null
                || !(mc.player.getVehicle() instanceof NgCityEntity car)) {
            for (var key : ACTION_KEYS) while (key.consumeClick()) {}
            return;
        }
        for (int i = 0; i < ACTION_KEYS.length; i++) while (ACTION_KEYS[i].consumeClick()) {
            if (mc.screen == null&&(car.getDriver()==mc.player||i==19||i==20)) VehicleNetwork.CHANNEL.send(new VehicleNetwork.Action(car.getId(), i), PacketDistributor.SERVER.noArg());
        }
        if(car.getDriver()!=mc.player)return;
        if (mc.screen == null && HORN.isDown() && mc.player.tickCount % 12 == 0)
            VehicleNetwork.CHANNEL.send(new VehicleNetwork.Action(car.getId(), VehicleNetwork.HORN_ACTION), PacketDistributor.SERVER.noArg());
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

    }

    private DrivingClient() {}
}
