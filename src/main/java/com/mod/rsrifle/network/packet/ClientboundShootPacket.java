package com.mod.rsrifle.network.packet;

import com.mod.rsrifle.client.RifleShootAnimHelper;
import com.mod.rsrifle.network.RSRiflePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public record ClientboundShootPacket(long shoterId, int chargeLevel) implements RSRiflePacket {

    public ClientboundShootPacket(FriendlyByteBuf buf) {
        this(buf.readVarLong(), buf.readVarInt());
    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {
        buf.writeVarLong(shoterId);
        buf.writeVarInt(chargeLevel);
    }

    @Override
    public void handle(Executor exec, PacketListener listener, @Nullable ServerPlayer sender) {
        exec.execute(() -> RifleShootAnimHelper.addShootingRifle(shoterId, chargeLevel));
    }
}