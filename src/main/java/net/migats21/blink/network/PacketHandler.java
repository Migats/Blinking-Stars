package net.migats21.blink.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class PacketHandler {

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(BiStarBlinkPacket.TYPE, BiStarBlinkPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSolarCursePacket.TYPE, ClientboundSolarCursePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundFallingStarPacket.TYPE, ClientboundFallingStarPacket::handle);
    }

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(BiStarBlinkPacket.TYPE, BiStarBlinkPacket::handle);
    }

    static {
        PayloadTypeRegistry<RegistryFriendlyByteBuf> s2c = PayloadTypeRegistry.playS2C();
        PayloadTypeRegistry<RegistryFriendlyByteBuf> c2s = PayloadTypeRegistry.playC2S();
        c2s.register(BiStarBlinkPacket.TYPE, BiStarBlinkPacket.CODEC);
        s2c.register(BiStarBlinkPacket.TYPE, BiStarBlinkPacket.CODEC);
        s2c.register(ClientboundFallingStarPacket.TYPE, ClientboundFallingStarPacket.CODEC);
        s2c.register(ClientboundSolarCursePacket.TYPE, ClientboundSolarCursePacket.CODEC);
    }
}
