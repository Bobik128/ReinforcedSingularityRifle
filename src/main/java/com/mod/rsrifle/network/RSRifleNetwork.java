package com.mod.rsrifle.network;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.network.packet.ClientboundShootPacket;
import com.mod.rsrifle.network.packet.ServerboundFirearmActionPacket;
import com.mod.rsrifle.network.packet.ServerboundSetAttackKeyPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;
import java.util.function.Supplier;

public class RSRifleNetwork {

    private static SimpleChannel NETWORK = construct();
    public static final String VERSION = "1.0.0";

    private static SimpleChannel construct() {
        SimpleChannel network = NetworkRegistry.ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(ReinforcedSingularityRifle.MODID,"network"))
                .clientAcceptedVersions(VERSION::equals)
                .serverAcceptedVersions(VERSION::equals)
                .networkProtocolVersion(() -> VERSION)
                .simpleChannel();

        int id = 0;

        buildMessage(network, id++, ServerboundSetAttackKeyPacket.class, ServerboundSetAttackKeyPacket::new);
        buildMessage(network, id++, ServerboundFirearmActionPacket.class, ServerboundFirearmActionPacket::new);
        buildMessage(network, id++, ClientboundShootPacket.class, ClientboundShootPacket::new);

        return network;
    }

    private static <MSG extends RSRiflePacket> void buildMessage(SimpleChannel network, int id, Class<MSG> msg, Function<FriendlyByteBuf, MSG> decoder) {
        network.messageBuilder(msg, id)
                .decoder(decoder)
                .encoder(RSRiflePacket::rootEncode)
                .consumerMainThread(RSRifleNetwork::consumeRBHPacket)
                .add();
    }

    private static <MSG extends RSRiflePacket> void consumeRBHPacket(MSG packet, Supplier<NetworkEvent.Context> sup) {
        NetworkEvent.Context ctx = sup.get();
        packet.handle(ctx::enqueueWork, ctx.getNetworkManager().getPacketListener(), ctx.getSender());
        ctx.setPacketHandled(true);
    }

    public static <MSG extends RSRiflePacket> void sendToServer(MSG msg) { NETWORK.sendToServer(msg); }

    public static <MSG extends RSRiflePacket> void sendToPlayer(MSG msg, ServerPlayer player) {
        NETWORK.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public static <MSG extends RSRiflePacket> void sendToAll(MSG msg) {
        NETWORK.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static <MSG extends RSRiflePacket> void sendToAllInDimension(MSG msg, Level level) {
        NETWORK.send(PacketDistributor.DIMENSION.with(level::dimension), msg);
    }

    public static void init() {}

}