package net.migats21.blink.mixin.client;

import net.migats21.blink.client.BlinkingStarsClient;
import net.migats21.blink.client.ConfigOptions;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionSpecialEffects.OverworldEffects.class)
public class MixinSolarCurse {
    @Inject(method = "getSunriseOrSunsetColor", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    public void getSolarCurseColor(float f, CallbackInfoReturnable<Integer> cir) {
        if (!ConfigOptions.CURSED_SUNCOLOR.get()) return;
        int color = cir.getReturnValue();
        int color1 = ARGB.color(ARGB.red(color), ARGB.blue(color), ARGB.red(color));
        cir.setReturnValue(ARGB.lerp(BlinkingStarsClient.getCurseProgress(), color, color1));
        cir.cancel();
    }
}
