package net.migats21.blink.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PacketHandler {
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundStarBlinkPacket.ID, ClientboundStarBlinkPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSolarCursePacket.ID, ClientboundSolarCursePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundFallingStarPacket.ID, ClientboundFallingStarPacket::handle);
    }

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(ServerboundStarBlinkPacket.ID, ServerboundStarBlinkPacket::handle);
    }
}
