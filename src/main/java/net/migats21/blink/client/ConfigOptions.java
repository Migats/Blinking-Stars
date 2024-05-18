package net.migats21.blink.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.migats21.blink.BlinkingStars;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Environment(EnvType.CLIENT)
public class ConfigOptions {
    public static final Properties CONFIG = new Properties();
    public static final OptionInstance<Boolean> ANIMATE_STARS = OptionInstance.createBoolean("options.blink.animate", true, (bl) -> {
        BlinkingStarsClient.animatedStars = bl;
        CONFIG.setProperty("blink.animate", bl.toString());
    });
    public static final OptionInstance<Boolean> COLORED_STARS = OptionInstance.createBoolean("options.blink.colors", true, (bl) -> {
        BlinkingStarsClient.coloredStars = bl;
        BlinkingStarsClient.shouldUpdateStars = true;
        CONFIG.setProperty("blink.colors", bl.toString());
    });
    public static final OptionInstance<Boolean> STAR_VARIETY = OptionInstance.createBoolean("options.blink.variety", true, (bl) -> {
        BlinkingStarsClient.starVariety = bl;
        BlinkingStarsClient.shouldUpdateStars = true;
        CONFIG.setProperty("blink.variety", bl.toString());
    });
    public static final OptionInstance<Boolean> ALLOW_FALLING_STARS = OptionInstance.createBoolean("options.blink.fallingstars", true, (bl) ->
        CONFIG.setProperty("blink.fallingstars", bl.toString())
    );
    public static final OptionInstance<Boolean> ALLOW_BLINK = OptionInstance.createBoolean("options.blink.enabled", OptionInstance.cachedConstantTooltip(Component.translatable("options.blink.enabled.description")), true, (bl) ->
        CONFIG.setProperty("blink.enabled", bl.toString())
    );
    public static final OptionInstance<Integer> BLINK_SENSITIVITY = new OptionInstance<>("options.blink.sensitivity", OptionInstance.noTooltip(), (component, object) -> Options.genericValueLabel(component, Component.literal(object == 16 ? "Default" : String.valueOf(object))), new OptionInstance.IntRange(0, 255), 16, (i) ->
        CONFIG.setProperty("blink.sensitivity", i.toString())
    );
    public static final OptionInstance<Boolean> CURSED_SKYDARKEN = OptionInstance.createBoolean("options.blink.solarcurse.skydarken", true, (bl) -> {
        BlinkingStarsClient.cursedSkyDarken = bl;
        CONFIG.setProperty("blink.solarcurse.skydarken", bl.toString());
    });
    public static final OptionInstance<Boolean> CURSED_SUNCOLOR = OptionInstance.createBoolean("options.blink.solarcurse.suncolor", true, (bl) -> {
        BlinkingStarsClient.cursedSunColor = bl;
        CONFIG.setProperty("blink.solarcurs.suncolor", bl.toString());
    });

    static void initConfig() {
        File configDir = FabricLoader.getInstance().getConfigDir().resolve(BlinkingStars.MODID).toFile();
        if (!configDir.exists()) {
            if (!configDir.mkdir()) {
                BlinkingStarsClient.LOGGER.warn("[Blink] Could not create configuration directory: " + configDir.getAbsolutePath());
            }
        }
        File configFile = new File(configDir, "blinking-stars.properties");
        if (configFile.exists()) {
            try (FileInputStream inputStream = new FileInputStream(configFile)) {
                CONFIG.load(inputStream);
            } catch (IOException exception) {
                BlinkingStarsClient.LOGGER.warn("[Blink] Could not read property file '" + configFile.getAbsolutePath() + "'", exception);
            }
        }

        ANIMATE_STARS.set(CONFIG.computeIfAbsent("blink.animate", (key) -> "true").equals("true"));
        COLORED_STARS.set(CONFIG.computeIfAbsent("blink.colors", (key) -> "true").equals("true"));
        STAR_VARIETY.set(CONFIG.computeIfAbsent("blink.variety", (key) -> "true").equals("true"));
        ALLOW_FALLING_STARS.set(CONFIG.computeIfAbsent("blink.fallingstars", (key) -> "true").equals("true"));
        ALLOW_BLINK.set(CONFIG.computeIfAbsent("blink.enabled", (key) -> "true").equals("true"));
        BLINK_SENSITIVITY.set(safeParseInt((String)CONFIG.computeIfAbsent("blink.sensitivity", (key) -> "16")) & 255);
        CURSED_SKYDARKEN.set(CONFIG.computeIfAbsent("blink.solarcurse.skydarken", (key) -> "true").equals("true"));
        CURSED_SUNCOLOR.set(CONFIG.computeIfAbsent("blink.solarcurse.suncolor", (key) -> "true").equals("true"));
        save();
    }

    public static void save() {
        File configDir = FabricLoader.getInstance().getConfigDir().resolve(BlinkingStars.MODID).toFile();
        File configFile = new File(configDir, "blinking-stars.properties");
        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            CONFIG.store(outputStream, "Blinking Stars config file");
        } catch (IOException exception) {
            BlinkingStarsClient.LOGGER.warn("[Blink] Could not store property file '" + configFile.getAbsolutePath() + "'", exception);
        }
    }

    private static int safeParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            BlinkingStarsClient.LOGGER.warn("[Blink] Invalid integer", exception);
            return 0;
        }
    }
}
