package net.migats21.blink.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.StarBlinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ClientboundStarBlinkPacket {
    public static final ResourceLocation ID = new ResourceLocation(BlinkingStars.MODID, "star");

    public static void handle(Minecraft minecraft, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender packetSender) {
        StarBlinker.isServerAllowed();
        if (buffer.readBoolean()) {
            float x = buffer.readFloat() * Mth.DEG_TO_RAD;
            float y = buffer.readFloat() * Mth.DEG_TO_RAD;
            StarBlinker.blink(minecraft, x, y);
        }
    }
}
