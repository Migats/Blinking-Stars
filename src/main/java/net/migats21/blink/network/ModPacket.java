package net.migats21.blink.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;

public interface ModPacket {
    void sendPayload(PacketSender sender);
}
