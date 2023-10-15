package net.migats21.blink.common;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class ModGameRules {
    public static final GameRules.Key<GameRules.BooleanValue> CURSED_SKY_DARKEN = GameRuleRegistry.register("cursedSkyDarken", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(false));
    public static final GameRules.Key<GameRules.IntegerValue> FALLING_STAR_INTERVAL = GameRuleRegistry.register("fallingStarInterval", GameRules.Category.UPDATES, GameRuleFactory.createIntRule(6000, 10, 48000, (server, i) -> ServerSavedData.getSavedData(server.getLevel(Level.OVERWORLD)).rerollFallingStars()));

    public static void register() {
        // Do nothing. Initialize static fields
    }
}
