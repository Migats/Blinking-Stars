package net.migats21.blink.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.payload.PacketByteBufPayload;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.StarBlinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ClientboundStarBlinkPacket {
    public static final ResourceLocation ID = new ResourceLocation(BlinkingStars.MODID, "star");
    private final float x, y;
    private byte s;

    public ClientboundStarBlinkPacket(float x, float y) {
        this.x = x;
        this.y = y;
        this.s = 10;
    }

    // TODO: Make the sensitivity configurable
    public ClientboundStarBlinkPacket(float x, float y, byte s) {
        this.x = x;
        this.y = y;
        this.s = s;
    }

    public static void handle(Minecraft minecraft, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender packetSender) {
        byte s = buffer.readByte();
        float x = buffer.readFloat() * Mth.DEG_TO_RAD;
        float y = buffer.readFloat() * Mth.DEG_TO_RAD;
        StarBlinker.blink(minecraft, s, x, y);
    }
    public Packet<ClientCommonPacketListener> asPayload() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeByte(s);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        return new ClientboundCustomPayloadPacket(new PacketByteBufPayload(ID, buffer));
    }
}
