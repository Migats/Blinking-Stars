package net.migats21.blink.common;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.migats21.blink.network.ClientboundFallingStarPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.saveddata.SavedData;

public class ServerSavedData extends SavedData {
    private boolean cursed;
    private static final RandomSource random = RandomSource.create();
    public int nextFallingStar;
    public ServerLevel level;
    public boolean isCursed() {
        return cursed;
    }
    public void setCursed(boolean b) {
        cursed = b;
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
        nextFallingStar = random.nextInt(6000);
    }

    public static ServerSavedData getSavedData(ServerLevel level) {
        ServerSavedData savedData = level.getDataStorage().computeIfAbsent(ServerSavedData::new, ServerSavedData::new, "blink");
        savedData.setDirty();
        savedData.level = level;
        return savedData;
    }
    public void tick() {
        nextFallingStar--;
        if (nextFallingStar <= 0) {
            nextFallingStar = random.nextInt(level.getGameRules().getInt(ModGameRules.FALLING_STAR_FREQUENCY));
            FriendlyByteBuf buffer = PacketByteBufs.create();
            buffer.writeFloat(random.nextFloat() * 2.0F - 1.0F);
            buffer.writeFloat(random.nextFloat() * 2.0F - 1.0F);
            buffer.writeFloat(random.nextFloat() * 2.0F - 1.0F);
            buffer.writeFloat(0.1F + random.nextFloat() * 0.1F);
            buffer.writeDouble(random.nextDouble() * Mth.PI * 2.0);
            for (ServerPlayer player : level.players()) {
                ServerPlayNetworking.send(player, ClientboundFallingStarPacket.ID, buffer);
            }
        }
    }

    public void rerollFallingStars() {
        nextFallingStar = random.nextInt(level.getGameRules().getInt(ModGameRules.FALLING_STAR_FREQUENCY));
    }
}
