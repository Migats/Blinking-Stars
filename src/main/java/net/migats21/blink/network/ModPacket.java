package net.migats21.blink.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ModPacket extends CustomPacketPayload {
    void write(FriendlyByteBuf buffer);

    default void handle(ServerPlayNetworking.Context context) {

    }

    default void handle(ClientPlayNetworking.Context context) {

    }
}
