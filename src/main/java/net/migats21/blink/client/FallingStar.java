package net.migats21.blink.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class FallingStar {
    public static final Minecraft minecraft = Minecraft.getInstance();
    public final Vector3f position;
    public final float angle;
    public final float size;
    private int time;
    private volatile static FallingStar fallingStar;

    public static FallingStar getInstance() {
        return fallingStar;
    }

    private FallingStar(float x, float y, float z, float size, float angle) {
        position = new Vector3f(x, y, z);
        float d = position.lengthSquared();
        if (d < 1.0f && d > 0.01f) {
            position.normalize(100.0f);
            this.size = size;
            this.angle = angle;
            this.time = 0;
        } else {
            position.set(0.0f);
            this.size = this.angle = 0.0f;
            this.time = 10;
        }
    }

    public static void add(float x, float y, float z, float size, float angle) {
        if (fallingStar == null) fallingStar = new FallingStar(x, y, z, size, angle);
    }

    public int getColor() {
        float f = Math.max(5.0f - Mth.abs(time-5.0f), 0.0f);
        return 0xffffff | ((int)(f * 255) << 24);
    }

    public float getOffset() {
        float frameTime = (float)time + minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        return (frameTime-5.0f)*20.0f;
    }

    public float getTailOffset() {
        float frameTime = (float)time + minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        return (frameTime*frameTime*frameTime-500)*0.2f;
    }

    public static void tick(ClientLevel ignoredLevel) {
        if (fallingStar == null) return;
        fallingStar.time++;
        if (fallingStar.time >= 10) fallingStar = null;
    }
}
