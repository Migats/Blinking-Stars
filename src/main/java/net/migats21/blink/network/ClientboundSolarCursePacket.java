package net.migats21.blink.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.BlinkingStarsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ClientboundSolarCursePacket implements ModPacket {
    public static final ResourceLocation ID = new ResourceLocation(BlinkingStars.MODID, "curse");
    private final boolean cursed;

    public ClientboundSolarCursePacket(boolean cursed) {
        this.cursed = cursed;
    }

    public static void handle(Minecraft minecraft, ClientPacketListener handler, FriendlyByteBuf buffer, PacketSender packetSender) {
        BlinkingStarsClient.cursed = buffer.readBoolean();
        BlinkingStarsClient.isOnServer = true;
    }

    public void sendPayload(PacketSender sender) {
        FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
        friendlyByteBuf.writeBoolean(cursed);
        sender.sendPacket(sender.createPacket(ID, friendlyByteBuf));
    }
}
