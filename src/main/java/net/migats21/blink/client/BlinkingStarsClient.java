package net.migats21.blink.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.migats21.blink.network.PacketHandler;

public class BlinkingStarsClient implements ClientModInitializer {
    public static boolean cursed = false;
    public static boolean isOnServer = false;

    @Override
    public void onInitializeClient() {
        PacketHandler.registerClient();
        StarBlinker.init();
        ClientPlayConnectionEvents.DISCONNECT.register((listener, minecraft) -> {
            StarBlinker.reset();
            cursed = false;
            isOnServer = false;
        });
        ClientTickEvents.START_WORLD_TICK.register(StarBlinker::tick);
        ClientTickEvents.START_WORLD_TICK.register(StarSweeper::tick);
    }
}
