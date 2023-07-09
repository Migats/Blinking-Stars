package net.migats21.blink.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.StarBlinker;
import net.migats21.blink.client.StarSweeper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ClientboundFallingStarPacket {
    public static final ResourceLocation ID = new ResourceLocation(BlinkingStars.MODID, "falling");

    public static void handle(Minecraft minecraft, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender packetSender) {
        StarSweeper.fallingStar(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readDouble());
    }
}
