package net.migats21.blink.client;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;

public class ConfigOptions {
    public static OptionInstance<Integer> BLINK_SENSITIVITY = new OptionInstance<>("options.blink.sensitivity",
            OptionInstance.noTooltip(), (component, object) -> Options.genericValueLabel(component, Component.literal(String.valueOf(object))), new OptionInstance.IntRange(0,255), 16, (i) -> {
        BlinkingStarsClient.CONFIG.setProperty("blink.sensitivity", i.toString());
        BlinkingStarsClient.storeConfig();
            });
}
