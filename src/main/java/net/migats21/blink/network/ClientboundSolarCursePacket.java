package net.migats21.blink.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.BlinkingStarsClient;
import net.migats21.blink.common.ServerSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSolarCursePacket {
    public static final ResourceLocation ID = new ResourceLocation(BlinkingStars.MODID, "curse");

    public static void handle(Minecraft minecraft, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender packetSender) {
        BlinkingStarsClient.cursed = buffer.readBoolean();
    }
}
