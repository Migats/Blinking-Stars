package net.migats21.blink.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.payload.PacketByteBufPayload;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.BlinkingStarsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSolarCursePacket {
    public static final ResourceLocation ID = new ResourceLocation(BlinkingStars.MODID, "curse");
    private final boolean cursed;

    public ClientboundSolarCursePacket(boolean cursed) {
        this.cursed = cursed;
    }

    public static void handle(Minecraft minecraft, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender packetSender) {
        BlinkingStarsClient.cursed = buffer.readBoolean();
        BlinkingStarsClient.isOnServer = true;
    }

    public Packet<ClientCommonPacketListener> asPayload() {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
        friendlyByteBuf.writeBoolean(cursed);
        return new ClientboundCustomPayloadPacket(new PacketByteBufPayload(ID, friendlyByteBuf));
    }
}
