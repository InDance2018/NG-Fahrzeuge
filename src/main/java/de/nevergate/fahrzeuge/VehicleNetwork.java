package de.nevergate.fahrzeuge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

/** Clients submit buttons only. Position, speed and collision are computed on the server. */
public final class VehicleNetwork {
    public static final int FORWARD = 1, REVERSE = 2, LEFT = 4, RIGHT = 8, BRAKE = 16;
    public static final int VALID_MASK = FORWARD | REVERSE | LEFT | RIGHT | BRAKE;
    public static final int PARK_ACTION = 0, LIGHT_ACTION = 1, ENGINE_ACTION = 2,
            LEFT_ACTION = 3, RIGHT_ACTION = 4, HAZARD_ACTION = 5, HORN_ACTION = 6, CARGO_ACTION = 7, DOOR_ACTION=8, HATCH_ACTION=9, TRIP_ACTION=10, CRUISE_ACTION=11, COUPLE_ACTION=12, WIPER_ACTION=13, WINDOW_ACTION=14, CABIN_ACTION=15,HOOD_ACTION=16,BEACON_ACTION=17,ROOF_ACTION=18;
    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(new ResourceLocation(NgFahrzeuge.MOD_ID, "driving"))
            .networkProtocolVersion(8).simpleChannel();

    public record DriveInput(int vehicleId, int buttons) {}
    public record Action(int vehicleId, int action) {}
    public record Manage(int vehicleId,int operation,String value) {}

    public static void register() {
        CHANNEL.messageBuilder(Manage.class,NetworkDirection.PLAY_TO_SERVER)
                .encoder((m,b)->{b.writeVarInt(m.vehicleId());b.writeByte(m.operation());b.writeUtf(m.value(),48);})
                .decoder(b->new Manage(b.readVarInt(),b.readUnsignedByte(),b.readUtf(48)))
                .consumerMainThread((m,c)->{if(c.getSender()!=null)VehicleManagement.edit(c.getSender(),m.vehicleId(),m.operation(),m.value());}).add();
        CHANNEL.messageBuilder(DriveInput.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(VehicleNetwork::encode)
                .decoder(VehicleNetwork::decode)
                .consumerMainThread((input, context) -> apply(context.getSender(), input))
                .add();
        CHANNEL.messageBuilder(Action.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder((input, buffer) -> { buffer.writeVarInt(input.vehicleId()); buffer.writeByte(input.action()); })
                .decoder(buffer -> new Action(buffer.readVarInt(), buffer.readUnsignedByte()))
                .consumerMainThread((input, context) -> applyAction(context.getSender(), input))
                .add();
    }

    public static void encode(DriveInput input, FriendlyByteBuf buffer) {
        buffer.writeVarInt(input.vehicleId());
        buffer.writeByte(input.buttons());
    }

    public static DriveInput decode(FriendlyByteBuf buffer) {
        return new DriveInput(buffer.readVarInt(), buffer.readUnsignedByte());
    }

    public static boolean apply(Player sender, DriveInput input) {
        if (sender == null || sender.level().isClientSide || !sender.isAlive()
                || sender.isSpectator() || !sender.getAbilities().mayBuild
                || (input.buttons() & ~VALID_MASK) != 0) return false;
        // No client-selected world lookup; the sender can affect only their actual vehicle.
        if (!(sender.getVehicle() instanceof NgCityEntity car) || car.getId() != input.vehicleId()) {
            return false;
        }
        return car.acceptInput(sender, input.buttons());
    }

    public static boolean applyAction(Player sender, Action input) {
        if (sender == null || sender.level().isClientSide || !sender.isAlive() || sender.isSpectator()
                || !(sender.getVehicle() instanceof NgCityEntity car)
                || car.getId() != input.vehicleId()) return false;
        return car.applyAction(sender, input.action());
    }

    private VehicleNetwork() {}
}
