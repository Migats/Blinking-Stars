package net.migats21.blink.mixin;

import net.migats21.blink.client.BlinkingStarsClient;
import net.migats21.blink.common.ModGameRules;
import net.migats21.blink.common.ServerSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Level.class)
public abstract class MixinSkyLightLevels implements LevelAccessor {
    @Shadow public abstract boolean isClientSide();

    @Shadow public abstract GameRules getGameRules();

    @Redirect(method = "updateSkyBrightness", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;cos(F)F", ordinal = 0))
    public float modifySkyBrightness(float f) {
        f = Mth.cos(f);
        if (this.isClientSide()) {
            return f - (BlinkingStarsClient.cursed ? 0.2f : 0.0f);
        } else if (getGameRules().getBoolean(ModGameRules.CURSED_SKY_DARKEN)) {
            return f - (ServerSavedData.getSavedData((ServerLevel)((LevelAccessor)this)).isCursed() ? 0.2f : 0.0f);
        }
        return f;
    }
}
