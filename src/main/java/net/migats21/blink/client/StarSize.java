package net.migats21.blink.client;

import net.minecraft.util.OptionEnum;
import org.jetbrains.annotations.NotNull;

public enum StarSize implements OptionEnum {
    SMALL(0.05f, "options.blink.size.small"),
    MEDIUM(0.1f, "options.blink.size.medium"),
    LARGE(0.15f, "options.blink.size.large");

    private final float value;
    private final String key;

    StarSize(float floatVal, String key) {
        this.value = floatVal;
        this.key = key;
    }

    public static StarSize byId(int id) {
        if (id > 2 || id < 0) return SMALL;
        return values()[id];
    }

    public float getAsFloat() {
        return value;
    }

    @Override
    public int getId() {
        return ordinal();
    }

    @Override
    public @NotNull String getKey() {
        return key;
    }
}
