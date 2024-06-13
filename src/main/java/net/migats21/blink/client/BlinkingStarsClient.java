package net.migats21.blink.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.migats21.blink.network.PacketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlinkingStarsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(BlinkingStarsClient.class);
    public static boolean cursed = false;
    public static boolean isOnServer = false;
    public static boolean shouldUpdateStars = false;

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
        ClientTickEvents.START_WORLD_TICK.register(FallingStar::tick);
        ConfigOptions.initConfig();
    }
}
