package net.migats21.blink.client;

import com.mojang.serialization.Codec;
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
import java.util.Arrays;
import java.util.Properties;

@Environment(EnvType.CLIENT)
public class ConfigOptions {
    public static final Properties CONFIG = new Properties();
    public static final OptionInstance<StarDensity> STAR_DENSITY = new OptionInstance<>("options.blink.density", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum<>(Arrays.asList(StarDensity.values()), Codec.INT.xmap(StarDensity::byId, StarDensity::getId)), StarDensity.MEDIUM, (s) -> {});
    public static final OptionInstance<StarSize> STAR_SIZE = new OptionInstance<>("options.blink.size", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum<>(Arrays.asList(StarSize.values()), Codec.INT.xmap(StarSize::byId, StarSize::getId)), StarSize.MEDIUM, (s) -> {});
    public static final OptionInstance<Boolean> ANIMATE_STARS = OptionInstance.createBoolean("options.blink.animate", true);
    public static final OptionInstance<Boolean> COLORED_STARS = OptionInstance.createBoolean("options.blink.colors", true, (bl) ->
        BlinkingStarsClient.shouldUpdateStars = true
    );
    public static final OptionInstance<Boolean> STAR_VARIETY = OptionInstance.createBoolean("options.blink.variety", true, (bl) ->
        BlinkingStarsClient.shouldUpdateStars = true
    );
    public static final OptionInstance<Boolean> ALLOW_FALLING_STARS = OptionInstance.createBoolean("options.blink.fallingstars", true);
    public static final OptionInstance<Boolean> ALLOW_BLINK = OptionInstance.createBoolean("options.blink.enabled", OptionInstance.cachedConstantTooltip(Component.translatable("options.blink.enabled.description")), true);
    public static final OptionInstance<Integer> BLINK_SENSITIVITY = new OptionInstance<>("options.blink.sensitivity", OptionInstance.noTooltip(), (component, object) -> Options.genericValueLabel(component, Component.literal(object == 16 ? "Default" : String.valueOf(object))), new OptionInstance.IntRange(0, 64), 16, (i) -> {});
    public static final OptionInstance<Boolean> CURSED_SKYDARKEN = OptionInstance.createBoolean("options.blink.solarcurse.skydarken", true);
    public static final OptionInstance<Boolean> CURSED_SUNCOLOR = OptionInstance.createBoolean("options.blink.solarcurse.suncolor", true);

    static void initConfig() {
        File configDir = FabricLoader.getInstance().getConfigDir().resolve(BlinkingStars.MODID).toFile();
        if (!configDir.exists()) {
            if (!configDir.mkdir()) {
                BlinkingStarsClient.LOGGER.warn("[Blink] Could not create configuration directory: {}", configDir.getAbsolutePath());
            }
        }
        File configFile = new File(configDir, "blinking-stars.properties");
        if (configFile.exists()) {
            try (FileInputStream inputStream = new FileInputStream(configFile)) {
                CONFIG.load(inputStream);
            } catch (IOException exception) {
                BlinkingStarsClient.LOGGER.warn("[Blink] Could not read property file '{}'", configFile.getAbsolutePath(), exception);
            }
        }
        STAR_DENSITY.set(StarDensity.byId(safeParseInt(CONFIG.getProperty("blink.density", "1"))));
        STAR_SIZE.set(StarSize.byId(safeParseInt(CONFIG.getProperty("blink.size", "1"))));
        ANIMATE_STARS.set(Boolean.parseBoolean(CONFIG.getProperty("blink.animate", "true")));
        COLORED_STARS.set(Boolean.parseBoolean(CONFIG.getProperty("blink.colors", "true")));
        STAR_VARIETY.set(Boolean.parseBoolean(CONFIG.getProperty("blink.variety", "true")));
        ALLOW_FALLING_STARS.set(Boolean.parseBoolean(CONFIG.getProperty("blink.fallingstars", "true")));
        ALLOW_BLINK.set(Boolean.parseBoolean(CONFIG.getProperty("blink.enabled", "true")));
        BLINK_SENSITIVITY.set(safeParseInt(CONFIG.getProperty("blink.sensitivity", "16")) & 255);
        CURSED_SKYDARKEN.set(Boolean.parseBoolean(CONFIG.getProperty("blink.solarcurse.skydarken", "true")));
        CURSED_SUNCOLOR.set(Boolean.parseBoolean(CONFIG.getProperty("blink.solarcurse.suncolor", "true")));
        save();
    }

    public static void save() {
        File configDir = FabricLoader.getInstance().getConfigDir().resolve(BlinkingStars.MODID).toFile();
        File configFile = new File(configDir, "blinking-stars.properties");
        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            CONFIG.setProperty("blink.density", String.valueOf(STAR_DENSITY.get().getId()));
            CONFIG.setProperty("blink.size", String.valueOf(STAR_SIZE.get().getId()));
            CONFIG.setProperty("blink.animate", ANIMATE_STARS.get().toString());
            CONFIG.setProperty("blink.colors", COLORED_STARS.get().toString());
            CONFIG.setProperty("blink.variety", STAR_VARIETY.get().toString());
            CONFIG.setProperty("blink.fallingstars", ALLOW_FALLING_STARS.get().toString());
            CONFIG.setProperty("blink.enabled", ALLOW_BLINK.get().toString());
            CONFIG.setProperty("blink.sensitivity", BLINK_SENSITIVITY.get().toString());
            CONFIG.setProperty("blink.solarcurse.skydarken", CURSED_SKYDARKEN.get().toString());
            CONFIG.setProperty("blink.solarcurse.suncolor", CURSED_SUNCOLOR.get().toString());
            CONFIG.store(outputStream, "Blinking Stars config file");
        } catch (IOException exception) {
            BlinkingStarsClient.LOGGER.warn("[Blink] Could not store property file '{}'", configFile.getAbsolutePath(), exception);
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
