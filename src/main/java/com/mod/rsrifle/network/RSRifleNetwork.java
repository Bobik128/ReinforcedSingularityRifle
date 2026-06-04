package com.mod.rsrifle.network;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.network.packet.ClientboundShootPacket;
import com.mod.rsrifle.network.packet.ServerboundFirearmActionPacket;
import com.mod.rsrifle.network.packet.ServerboundSetAttackKeyPacket;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class RSRifleNetwork {
    private static final String VERSION = "1";

    private RSRifleNetwork() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RSRifleNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(ReinforcedSingularityRifle.MODID)
                .versioned(VERSION);

        registrar.playToServer(
                ServerboundSetAttackKeyPacket.TYPE,
                ServerboundSetAttackKeyPacket.STREAM_CODEC,
                ServerboundSetAttackKeyPacket::handle
        );

        registrar.playToServer(
                ServerboundFirearmActionPacket.TYPE,
                ServerboundFirearmActionPacket.STREAM_CODEC,
                ServerboundFirearmActionPacket::handle
        );

        registrar.playToClient(
                ClientboundShootPacket.TYPE,
                ClientboundShootPacket.STREAM_CODEC,
                ClientboundShootPacket::handle
        );
    }

    public static void sendToAllInDimension(ClientboundShootPacket packet, net.minecraft.world.level.Level level) {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersInDimension(serverLevel, packet);
        }
    }
}