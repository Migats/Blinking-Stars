package net.migats21.blink.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.ConfigOptions;
import net.migats21.blink.client.StarBlinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class BiStarBlinkPacket implements ModPacket {
    public static final ResourceLocation ID = new ResourceLocation(BlinkingStars.MODID, "star");
    private final float x, y;
    private final byte s;

    public BiStarBlinkPacket(float x, float y) {
        this.s = 10;
        this.x = x;
        this.y = y;
    }

    public BiStarBlinkPacket(byte s, float x, float y) {
        this.s = s;
        this.x = x;
        this.y = y;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListener listener, FriendlyByteBuf buffer, PacketSender sender) {
        player.serverLevel().players().forEach((player1) -> {
            if (player != player1) player1.connection.send(sender.createPacket(ID, buffer));
        });
    }

    public static void handle(Minecraft minecraft, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender packetSender) {
        if (ConfigOptions.ALLOW_BLINK.get()) {
            byte s = buffer.readByte();
            float x = buffer.readFloat();
            float y = buffer.readFloat();
            StarBlinker.blink(minecraft, s & 255, x, y);
        }
    }

    public void sendPayload(PacketSender sender) {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeByte(s);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        sender.sendPacket(sender.createPacket(ID, buffer));
    }
}
