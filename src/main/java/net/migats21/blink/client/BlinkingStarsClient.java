package net.migats21.blink.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.migats21.blink.network.PacketHandler;

public class BlinkingStarsClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        PacketHandler.register();
        StarBlinker.init();
        ClientPlayConnectionEvents.DISCONNECT.register((listener, minecraft) -> {
            StarBlinker.reset();
            cursed = false;
        });
        ClientTickEvents.START_WORLD_TICK.register(StarBlinker::tick);
        ClientTickEvents.START_WORLD_TICK.register(StarSweeper::tick);
    }
    public static boolean cursed = false;
}
