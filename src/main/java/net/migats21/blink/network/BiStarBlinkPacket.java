package net.migats21.blink.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.StarBlinker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BiStarBlinkPacket implements ModPacket {
    public static final Type<BiStarBlinkPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(BlinkingStars.MODID, "star"));
    public static final StreamCodec<FriendlyByteBuf, BiStarBlinkPacket> CODEC = StreamCodec.ofMember(BiStarBlinkPacket::write, BiStarBlinkPacket::new);

    private final float x, y;
    private final byte s;

    public BiStarBlinkPacket(float x, float y) {
        this.s = 10;
        this.x = x;
        this.y = y;
    }

    public BiStarBlinkPacket(byte s, float x, float y) {
        this.s = s;
        this.x = x;
        this.y = y;
    }

    public BiStarBlinkPacket(FriendlyByteBuf buffer) {
        s = buffer.readByte();
        x = buffer.readFloat();
        y = buffer.readFloat();
    }

    public void handle(ServerPlayNetworking.Context context) {
        context.player().serverLevel().players().forEach((player1) -> {
            if (context.player() != player1) player1.connection.send(context.responseSender().createPacket(this));
        });
    }

    public void handle(ClientPlayNetworking.Context context) {
        StarBlinker.blink(s & 255, x, y);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeByte(s);
        buffer.writeFloat(x);
        buffer.writeFloat(y);
    }

    public @NotNull Type<BiStarBlinkPacket> type() {
        return TYPE;
    }
}
