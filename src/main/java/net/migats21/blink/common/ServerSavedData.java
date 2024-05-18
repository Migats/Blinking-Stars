package net.migats21.blink.common;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.migats21.blink.network.ClientboundFallingStarPacket;
import net.migats21.blink.network.ClientboundSolarCursePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

public class ServerSavedData extends SavedData {
    private boolean cursed;
    private static final Factory<ServerSavedData> FACTORY = new Factory<>(ServerSavedData::new, ServerSavedData::new, DataFixTypes.LEVEL);
    private static final RandomSource RANDOM = RandomSource.create();
    public int nextFallingStar;
    public ServerLevel level;

    public static void syncCurse(PacketSender sender, ServerLevel level) {
        new ClientboundSolarCursePacket(getSavedData(level).isCursed()).sendPayload(sender);
    }

    public boolean isCursed() {
        return cursed;
    }

    public void setCursed(boolean b) {
        cursed = b;
        ClientboundSolarCursePacket packet = new ClientboundSolarCursePacket(cursed);
        level.players().forEach((player) -> packet.sendPayload(ServerPlayNetworking.getSender(player)));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putBoolean("cursed", cursed);
        compoundTag.putInt("nextFallingStar", nextFallingStar);
        return compoundTag;
    }

    public ServerSavedData(CompoundTag compoundTag) {
        cursed = compoundTag.getBoolean("cursed");
        nextFallingStar = compoundTag.getInt("nextFallingStar");
    }

    public ServerSavedData() {
        cursed = false;
        nextFallingStar = RANDOM.nextInt(6000);
    }

    public static ServerSavedData getSavedData(ServerLevel level) {
        ServerSavedData savedData = level.getDataStorage().computeIfAbsent(FACTORY, "blink");
        savedData.setDirty();
        savedData.level = level;
        return savedData;
    }

    public void tick() {
        nextFallingStar--;
        if (nextFallingStar <= 0) {
            rerollFallingStars();
            ClientboundFallingStarPacket fallingStarPacket = new ClientboundFallingStarPacket(RANDOM.nextFloat() * 2.0f - 1.0f, RANDOM.nextFloat() * 2.0f - 1.0f, RANDOM.nextFloat() * 2.0f - 1.0f, 0.1f + RANDOM.nextFloat() * 0.1f, RANDOM.nextDouble() * Mth.PI * 2.0);
            for (ServerPlayer player : level.players()) {
                fallingStarPacket.sendPayload(ServerPlayNetworking.getSender(player));
            }
        }
    }

    public void rerollFallingStars() {
        nextFallingStar = RANDOM.nextInt(level.getGameRules().getInt(ModGameRules.FALLING_STAR_INTERVAL));
    }
}
