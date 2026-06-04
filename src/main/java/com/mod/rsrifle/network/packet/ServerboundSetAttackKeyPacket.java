package com.mod.rsrifle.network.packet;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.api.HoldAttackKeyInteraction;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundSetAttackKeyPacket(boolean down) implements CustomPacketPayload {
    public static final Type<ServerboundSetAttackKeyPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ReinforcedSingularityRifle.MODID, "set_attack_key"));

    public static final StreamCodec<ByteBuf, ServerboundSetAttackKeyPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    ServerboundSetAttackKeyPacket::down,
                    ServerboundSetAttackKeyPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundSetAttackKeyPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sender)) {
                return;
            }

            ItemStack mainHandItem = sender.getMainHandItem();

            if (mainHandItem.getItem() instanceof HoldAttackKeyInteraction interactable) {
                if (packet.down()) {
                    interactable.onPressAttackKey(mainHandItem, sender);
                } else {
                    interactable.onReleaseAttackKey(mainHandItem, sender);
                }
            }
        });
    }
}