package net.migats21.blink.client;

import net.minecraft.util.OptionEnum;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntSupplier;

public enum StarDensity implements OptionEnum, IntSupplier {
    LOW(1500, "options.blink.density.low"),
    MEDIUM(3000, "options.blink.density.medium"),
    HIGH(7000, "options.blink.density.high"),
    EXTREME(15000, "options.blink.density.extreme");

    private final int value;
    private final String key;

    StarDensity(int intVal, String key) {
        this.value = intVal;
        this.key = key;
    }

    public static StarDensity byId(int id) {
        if ((id | 3) != 3) return MEDIUM;
        return values()[id];
    }

    @Override
    public int getAsInt() {
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
