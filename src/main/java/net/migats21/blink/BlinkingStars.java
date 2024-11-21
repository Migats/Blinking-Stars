package net.migats21.blink;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.migats21.blink.common.ModGameRules;
import net.migats21.blink.common.ServerSavedData;
import net.migats21.blink.common.command.BlinkCommand;
import net.migats21.blink.network.ClientboundSolarCursePacket;
import net.migats21.blink.network.PacketHandler;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class BlinkingStars implements ModInitializer {
    public static final String MODID = "blink";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(BlinkCommand::register);
        ServerPlayConnectionEvents.JOIN.register((listener, sender, server) ->
            ServerSavedData.syncCurse(sender, listener.player.serverLevel())
        );
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, level, level2) -> {
            ServerSavedData solarCurseData = ServerSavedData.getSavedData(level2);
            ServerPlayNetworking.send(player, new ClientboundSolarCursePacket(solarCurseData.isCursed()));
        });
        ServerTickEvents.START_WORLD_TICK.register((level) -> ServerSavedData.getSavedData(level).tick());
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> {
            long l = player.level().dayTime() % 24000;
            return (l > 12550 && l < 23450) || player.level().isThundering() ? InteractionResult.CONSUME : InteractionResult.FAIL;
        });
        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            if (livingEntity instanceof WitherBoss && !livingEntity.level().isClientSide) {
                ServerSavedData solarCurseData = ServerSavedData.getSavedData(livingEntity.getServer().overworld());
                solarCurseData.setCursed(false);
            }
        });
        ModGameRules.register();
        PacketHandler.registerServer();
    }
}
