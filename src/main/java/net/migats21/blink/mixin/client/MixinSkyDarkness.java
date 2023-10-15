package net.migats21.blink.mixin.client;

import net.migats21.blink.client.BlinkingStarsClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientLevel.class)
public class MixinSkyDarkness {
    @Redirect(method = "getSkyDarken", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;cos(F)F", ordinal = 0))
    public float modifySkyDarken(float f) {
        return Mth.cos(f) - (BlinkingStarsClient.cursed ? 0.2f : 0.0f);
    }

    @Redirect(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;cos(F)F", ordinal = 0))
    public float modifySkyColor(float f) {
        return Mth.cos(f) - (BlinkingStarsClient.cursed ? 0.2f : 0.0f);
    }

    @Redirect(method = "getCloudColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;cos(F)F", ordinal = 0))
    public float modifyCloudColor(float f) {
        return Mth.cos(f) - (BlinkingStarsClient.cursed ? 0.2f : 0.0f);
    }

    @Redirect(method = "getStarBrightness", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;cos(F)F", ordinal = 0))
    public float modifyStarBrightness(float f) {
        return Mth.cos(f) - (BlinkingStarsClient.cursed ? 0.2f : 0.0f);
    }
}
