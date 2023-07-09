package net.migats21.blink.mixin.client;

import net.migats21.blink.client.BlinkingStarsClient;
import net.migats21.blink.common.ServerSavedData;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FogRenderer.class)
public class MixinSkyFog {
    @Redirect(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;cos(F)F", ordinal = 0))
    private static float modifyFogDarkness(float f) {
        return Mth.cos(f) - (BlinkingStarsClient.cursed ? 0.2f : 0.0f);
    }
}
