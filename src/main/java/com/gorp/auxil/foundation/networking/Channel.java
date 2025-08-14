package com.gorp.auxil.foundation.networking;

import com.gorp.auxil.Auxiliaries;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public enum Channel {

    LINK_RADIANT_CHASSIS(RadiantChassisPacket.class, RadiantChassisPacket::new, PLAY_TO_SERVER),
    LINK_SHADOW_CHASSIS(ShadowChassisPacket.class, ShadowChassisPacket::new, PLAY_TO_SERVER),
    UPDATE_PROTECTED_SHADOW_CHASSIS_AREA(ShadowChassisAreaPacket.class, ShadowChassisAreaPacket::new, PLAY_TO_SERVER);

    public static SimpleChannel channel;
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(Auxiliaries.MODID, "network");
    public static final String NETWORK_VERSION = new ResourceLocation(Auxiliaries.MODID, "1").toString();

    private LoadedPacket<?> packet;

    private <T extends SimplePacketBase> Channel(Class<T> type, Function<PacketBuffer, T> factory,
                                                    NetworkDirection direction) {
        packet = new LoadedPacket<>(type, factory, direction);
    }

    public static void registerPackets() {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .serverAcceptedVersions(NETWORK_VERSION::equals)
                .clientAcceptedVersions(NETWORK_VERSION::equals)
                .networkProtocolVersion(() -> NETWORK_VERSION)
                .simpleChannel();

        for (Channel packet : values())
            packet.packet.register();
    }

    private static class LoadedPacket<T extends SimplePacketBase> {
        private static int index = 0;
        BiConsumer<T, PacketBuffer> encoder;
        Function<PacketBuffer, T> decoder;
        BiConsumer<T, Supplier<Context>> handler;
        Class<T> type;
        NetworkDirection direction;

        private LoadedPacket(Class<T> type, Function<PacketBuffer, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = T::handle;
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            channel.messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumer(handler)
                    .add();
        }
    }

}
