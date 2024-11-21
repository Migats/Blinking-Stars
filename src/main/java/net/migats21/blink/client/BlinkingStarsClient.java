package net.migats21.blink.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.migats21.blink.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlinkingStarsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(BlinkingStarsClient.class);
    public static boolean cursed = false;
    public static boolean isOnServer = false;
    public static boolean shouldUpdateStars = false;
    public static int curseTick = 0;

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
        ClientTickEvents.START_WORLD_TICK.register((clientLevel) -> {
            if (curseTick > 0) curseTick--;
        });
        ConfigOptions.initConfig();
    }

    public static float getCurseProgress() {
        float f = (curseTick - Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false)) / 50.0f;
        f = Mth.clamp(f, 0.0f, 1.0f);
        return cursed ? 1.0f - f : f;
    }
}
