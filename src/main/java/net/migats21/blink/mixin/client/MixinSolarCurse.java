package net.migats21.blink.mixin.client;

import net.migats21.blink.client.BlinkingStarsClient;
import net.migats21.blink.client.ConfigOptions;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionSpecialEffects.class)
public class MixinSolarCurse {
    @Inject(method = "getSunriseColor(FF)[F", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    public void getSolarCurseColor(float f, float g, CallbackInfoReturnable<float[]> cir) {
        if (BlinkingStarsClient.cursed && ConfigOptions.CURSED_SUNCOLOR.get()) {
            float[] arr = cir.getReturnValue();
            arr[1] = arr[2];
            arr[2] = arr[0];
            cir.setReturnValue(arr);
            cir.cancel();
        }
    }
}
