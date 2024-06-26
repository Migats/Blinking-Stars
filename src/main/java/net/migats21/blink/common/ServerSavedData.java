package net.migats21.blink.common;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.migats21.blink.network.ClientboundFallingStarPacket;
import net.migats21.blink.network.ClientboundSolarCursePacket;
import net.minecraft.core.HolderLookup;
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

    private ServerSavedData(CompoundTag compoundTag, HolderLookup.Provider provider) {
        cursed = compoundTag.getBoolean("cursed");
        nextFallingStar = compoundTag.getInt("nextFallingStar");
    }

    private ServerSavedData() {
        cursed = false;
        nextFallingStar = RANDOM.nextInt(6000);
    }

    public static ServerSavedData getSavedData(ServerLevel level) {
        ServerSavedData savedData = level.getDataStorage().computeIfAbsent(FACTORY, "blink");
        savedData.setDirty();
        savedData.level = level;
        return savedData;
    }

    public static void syncCurse(PacketSender sender, ServerLevel level) {
        sender.sendPacket(new ClientboundSolarCursePacket(getSavedData(level).isCursed()));
    }

    public boolean isCursed() {
        return cursed;
    }

    public void setCursed(boolean b) {
        cursed = b;
        ClientboundSolarCursePacket packet = new ClientboundSolarCursePacket(cursed);
        level.players().forEach((player) -> ServerPlayNetworking.getSender(player).sendPacket(packet));
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putBoolean("cursed", cursed);
        compoundTag.putInt("nextFallingStar", nextFallingStar);
        return compoundTag;
    }

    public void tick() {
        nextFallingStar--;
        if (nextFallingStar <= 0) {
            rerollFallingStars();
            ClientboundFallingStarPacket packet = new ClientboundFallingStarPacket(RANDOM.nextFloat() * 2.0f - 1.0f, RANDOM.nextFloat() * 2.0f - 1.0f, RANDOM.nextFloat() * 2.0f - 1.0f, 0.1f + RANDOM.nextFloat() * 0.1f, RANDOM.nextFloat() * Mth.PI * 2.0f);
            for (ServerPlayer player : level.players()) {
                ServerPlayNetworking.getSender(player).sendPacket(packet);
            }
        }
    }

    public void rerollFallingStars() {
        nextFallingStar = RANDOM.nextInt(level.getGameRules().getInt(ModGameRules.FALLING_STAR_INTERVAL));
    }
}
