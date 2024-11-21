package net.migats21.blink.mixin;

import net.migats21.blink.common.ModGameRules;
import net.migats21.blink.common.ServerSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MixinMobBurning extends LivingEntity {
    protected MixinMobBurning(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    public void canBurn(CallbackInfoReturnable<Boolean> cir) {
        if (this.level().isClientSide) return;
        if (((ServerLevel) this.level()).getGameRules().getBoolean(ModGameRules.CURSED_SKY_DARKEN) && ServerSavedData.getSavedData((ServerLevel)this.level()).isCursed()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
