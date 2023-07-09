package net.migats21.blink.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class PacketHandler {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundStarBlinkPacket.ID, ClientboundStarBlinkPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSolarCursePacket.ID, ClientboundSolarCursePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundFallingStarPacket.ID, ClientboundFallingStarPacket::handle);
    }
}
