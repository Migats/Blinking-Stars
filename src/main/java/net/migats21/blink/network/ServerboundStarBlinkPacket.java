package net.migats21.blink.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.payload.PacketByteBufPayload;
import net.migats21.blink.BlinkingStars;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ServerboundStarBlinkPacket {
    public static final ResourceLocation ID = new ResourceLocation(BlinkingStars.MODID, "blink");
    private final float x, y;
    private byte s;

    public ServerboundStarBlinkPacket(float x, float y, byte s) {
        this.x = x;
        this.y = y;
        this.s = s;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListener listener, FriendlyByteBuf buffer, PacketSender sender) {
        ClientboundStarBlinkPacket packet = new ClientboundStarBlinkPacket(buffer.readFloat(), buffer.readFloat(), buffer.readByte());
        player.serverLevel().players().forEach((player1) -> {
            if (player != player1) player1.connection.send(packet.asPayload());
        });
    }

    public Packet<ServerCommonPacketListener> asPayload() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeByte(s);
        return new ServerboundCustomPayloadPacket(new PacketByteBufPayload(ID, buffer));
    }
}
