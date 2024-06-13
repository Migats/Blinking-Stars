package net.migats21.blink.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.BlinkingStarsClient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ClientboundSolarCursePacket implements ModPacket {
    public static final Type<ClientboundSolarCursePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BlinkingStars.MODID, "curse"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundSolarCursePacket> CODEC = StreamCodec.ofMember(ClientboundSolarCursePacket::write, ClientboundSolarCursePacket::new);

    private final boolean cursed;

    public ClientboundSolarCursePacket(boolean cursed) {
        this.cursed = cursed;
    }

    public ClientboundSolarCursePacket(FriendlyByteBuf buffer) {
        this.cursed = buffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(cursed);
    }

    @Override
    public void handle(ClientPlayNetworking.Context context) {
        BlinkingStarsClient.cursed = cursed;
        BlinkingStarsClient.isOnServer = true;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
