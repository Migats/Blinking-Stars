package net.migats21.blink.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.payload.PacketByteBufPayload;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.StarSweeper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

public class ClientboundFallingStarPacket {
    public static final ResourceLocation ID = new ResourceLocation(BlinkingStars.MODID, "falling");
    private final float x, y, z, size;
    private final double angle;

    public ClientboundFallingStarPacket(float x, float y, float z, float size, double angle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.angle = angle;
    }

    public static void handle(Minecraft minecraft, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender packetSender) {
        StarSweeper.fallingStar(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readDouble());
    }

    public Packet<ClientCommonPacketListener> asPayload() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeFloat(z);
        buffer.writeFloat(size);
        buffer.writeDouble(angle);
        return new ClientboundCustomPayloadPacket(new PacketByteBufPayload(ID, buffer));
    }
}
