package net.migats21.blink.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.ConfigOptions;
import net.migats21.blink.client.FallingStar;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ClientboundFallingStarPacket implements ModPacket {
    public static final Type<ClientboundFallingStarPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BlinkingStars.MODID, "falling"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundFallingStarPacket> CODEC = StreamCodec.ofMember(ClientboundFallingStarPacket::write, ClientboundFallingStarPacket::new);

    private final float x, y, z, size;
    private final float angle;

    public ClientboundFallingStarPacket(float x, float y, float z, float size, float angle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.angle = angle;
    }

    public ClientboundFallingStarPacket(FriendlyByteBuf buffer) {
        this.x = buffer.readFloat();
        this.y = buffer.readFloat();
        this.z = buffer.readFloat();
        this.size = buffer.readFloat();
        this.angle = buffer.readFloat();
    }

    public void handle(ClientPlayNetworking.Context context) {
        if (ConfigOptions.ALLOW_FALLING_STARS.get()) {
            FallingStar.add(x, y, z, size, angle);
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeFloat(z);
        buffer.writeFloat(size);
        buffer.writeFloat(angle);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
