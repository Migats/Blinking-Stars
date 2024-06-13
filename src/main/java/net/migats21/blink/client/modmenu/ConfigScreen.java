package net.migats21.blink.client.modmenu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.migats21.blink.client.BlinkingStarsClient;
import net.migats21.blink.client.ConfigOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ConfigScreen extends OptionsSubScreen {
    protected ConfigScreen(Screen screen) {
        super(screen, Minecraft.getInstance().options, Component.translatable("options.blink"));
    }

    private static OptionInstance<?>[] initOptions() {
        List<OptionInstance<?>> list = new ArrayList<>();
        for (Field field : ConfigOptions.class.getFields()) {
            try {
                Object value = field.get(null);
                if (value instanceof OptionInstance<?>)
                    list.add((OptionInstance<?>) value);
            } catch (IllegalAccessException exception) {
                BlinkingStarsClient.LOGGER.warn("Option {} not loaded", field.getName());
            }
        }
        return list.toArray(OptionInstance[]::new);
    }

    @Override
    protected void addOptions() {
        list.addSmall(initOptions());
    }

    @Override
    public void removed() {
        ConfigOptions.save();
    }
}
