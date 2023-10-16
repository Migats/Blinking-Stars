package net.migats21.blink.client.modmenu;

import com.google.common.collect.ImmutableMap;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.function.Consumer;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ImmutableMap.of("blink", getModConfigScreenFactory());
    }

    @Override
    public void attachModpackBadges(Consumer<String> consumer) {
        ModMenuApi.super.attachModpackBadges(consumer);
    }
}
