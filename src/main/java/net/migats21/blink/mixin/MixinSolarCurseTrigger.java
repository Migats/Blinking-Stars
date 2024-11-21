package net.migats21.blink.mixin;

import net.migats21.blink.common.ModGameRules;
import net.migats21.blink.common.ServerSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EndDragonFight.class)
public class MixinSolarCurseTrigger {
    @Shadow private @Nullable UUID dragonUUID;

    @Shadow @Final private ServerLevel level;

    @Inject(method = "setDragonKilled", at = @At(value = "TAIL"))
    public void applySolarCurse(EnderDragon enderDragon, CallbackInfo ci) {
        if (enderDragon.getUUID().equals(this.dragonUUID) && level.getGameRules().getBoolean(ModGameRules.SOLAR_CURSE)) {
            ServerSavedData solarCurseData = ServerSavedData.getSavedData(level.getServer().overworld());
            solarCurseData.setCursed(true);
        }
    }
}
