package net.migats21.blink;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.migats21.blink.common.ModGameRules;
import net.migats21.blink.common.ServerSavedData;
import net.migats21.blink.common.command.BlinkCommand;
import net.migats21.blink.common.command.CurseCommand;
import net.migats21.blink.network.ClientboundSolarCursePacket;
import net.migats21.blink.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionResult;

public class BlinkingStars implements ModInitializer {
    public static final String MODID = "blink";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(BlinkCommand::register);
        CommandRegistrationCallback.EVENT.register(CurseCommand::register);
        ServerPlayConnectionEvents.JOIN.register((listener, sender, server) ->
            ServerSavedData.syncCurse(sender, listener.player.serverLevel())
        );
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, level, level2) -> {
            ServerSavedData solarCurseData = ServerSavedData.getSavedData(level2);
            FriendlyByteBuf buffer = PacketByteBufs.create();
            buffer.writeBoolean(solarCurseData.isCursed());
            ServerPlayNetworking.send(player, ClientboundSolarCursePacket.ID, buffer);
        });
        ServerTickEvents.START_WORLD_TICK.register((level) -> ServerSavedData.getSavedData(level).tick());
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanillaResult) -> {
            long l = player.level().dayTime() % 24000;
            return (l > 12550 && l < 23450) || player.level().isThundering() ? InteractionResult.sidedSuccess(player.level().isClientSide) : InteractionResult.FAIL;
        });
        ModGameRules.register();
        PacketHandler.registerServer();
    }
}
