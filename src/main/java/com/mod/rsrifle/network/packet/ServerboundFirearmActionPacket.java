package com.mod.rsrifle.network.packet;

import com.mod.rsrifle.ReinforcedSingularityRifle;
import com.mod.rsrifle.items.SingularityRifle;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundFirearmActionPacket(SingularityRifle.Action action) implements CustomPacketPayload {
    public static final Type<ServerboundFirearmActionPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ReinforcedSingularityRifle.MODID, "firearm_action"));

    private static final StreamCodec<ByteBuf, SingularityRifle.Action> ACTION_CODEC =
            new StreamCodec<>() {
                @Override
                public SingularityRifle.Action decode(ByteBuf buffer) {
                    int ordinal = ByteBufCodecs.VAR_INT.decode(buffer);
                    SingularityRifle.Action[] values = SingularityRifle.Action.values();

                    if (ordinal < 0 || ordinal >= values.length) {
                        return SingularityRifle.Action.CHARGE_END;
                    }

                    return values[ordinal];
                }

                @Override
                public void encode(ByteBuf buffer, SingularityRifle.Action action) {
                    ByteBufCodecs.VAR_INT.encode(buffer, action.ordinal());
                }
            };

    public static final StreamCodec<ByteBuf, ServerboundFirearmActionPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ACTION_CODEC,
                    ServerboundFirearmActionPacket::action,
                    ServerboundFirearmActionPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundFirearmActionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sender)) {
                return;
            }

            ItemStack mainHandItem = sender.getMainHandItem();

            if (mainHandItem.getItem() instanceof SingularityRifle mainFirearm) {
                switch (packet.action()) {
                    case RELOAD -> mainFirearm.onReload(mainHandItem, sender);
                    case CHARGE_START -> mainFirearm.chargeStart(mainHandItem, sender);
                    case CHARGE_END -> mainFirearm.chargeEnd(mainHandItem, sender);
                }
            }
        });
    }
}