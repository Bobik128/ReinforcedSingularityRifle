package com.mod.rsrifle.network.packet;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.client.RifleShootAnimHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundShootPacket(long shoterId, int chargeLevel) implements CustomPacketPayload {
    public static final Type<ClientboundShootPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ReinforcedSingularityRifle.MODID, "shoot"));

    public static final StreamCodec<ByteBuf, ClientboundShootPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG,
                    ClientboundShootPacket::shoterId,
                    ByteBufCodecs.VAR_INT,
                    ClientboundShootPacket::chargeLevel,
                    ClientboundShootPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientboundShootPacket packet, IPayloadContext context) {
        context.enqueueWork(() ->
                RifleShootAnimHelper.addShootingRifle(packet.shoterId(), packet.chargeLevel())
        );
    }
}