package net.migats21.blink.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.network.PacketHandler;
import net.minecraft.client.OptionInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class BlinkingStarsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(BlinkingStarsClient.class);
    public static boolean cursed = false;
    public static boolean isOnServer = false;
    public static final Properties CONFIG = new Properties();
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
        initConfig();
    }

    private void initConfig() {
        File configDir = FabricLoader.getInstance().getConfigDir().resolve(BlinkingStars.MODID).toFile();
        if (!configDir.exists()) {
            if (!configDir.mkdir()) {
                LOGGER.warn("[Blink] Could not create configuration directory: " + configDir.getAbsolutePath());
            }
        }
        File configFile = new File(configDir, "blinking-stars.properties");
        if (configFile.exists()) {
            try (FileInputStream inputStream = new FileInputStream(configFile)) {
                CONFIG.load(inputStream);
            } catch (IOException exception) {
                LOGGER.warn("[Blink] Could not read property file '" + configFile.getAbsolutePath() + "'", exception);
            }
        }

        ConfigOptions.BLINK_SENSITIVITY.set(safeParseInt((String) CONFIG.computeIfAbsent("blink.sensitivity", (key) -> "16")) & 255);

        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            CONFIG.store(outputStream, "Blinking Stars config file");
        } catch (IOException exception) {
            LOGGER.warn("[Blink] Could not store property file '" + configFile.getAbsolutePath() + "'", exception);
        }
    }

    public static void storeConfig() {
        File configDir = FabricLoader.getInstance().getConfigDir().resolve(BlinkingStars.MODID).toFile();
        File configFile = new File(configDir, "blinking-stars.properties");
        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            CONFIG.store(outputStream, "Blinking Stars config file");
        } catch (IOException exception) {
            LOGGER.warn("[Blink] Could not store property file '" + configFile.getAbsolutePath() + "'", exception);
        }
    }

    private int safeParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            LOGGER.warn("[Blink] Invalid integer", exception);
            return 0;
        }
    }
}
