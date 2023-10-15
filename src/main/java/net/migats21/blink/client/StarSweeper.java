package net.migats21.blink.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class StarSweeper {
    public static final Minecraft minecraft = Minecraft.getInstance();
    public final double x;
    public final double y;
    public final double z;
    public final double angle;
    public final double size;
    private int time;
    private volatile static StarSweeper starSweeper;

    public static StarSweeper getInstance() {
        return starSweeper;
    }

    private StarSweeper(float x, float y, float z, float size, double angle) {
        double d = x*x+y*y+z*z;
        if (d < 1.0 && d > 0.01) {
            d = 1.0 / Math.sqrt(d);
            this.x = x*d;
            this.y = y*d;
            this.z = z*d;
            this.size = size;
            this.angle = angle;
            this.time = 0;
        } else {
            this.x = this.y = this.z = this.size = this.angle = 0;
            this.time = 10;
        }
    }

    public static void fallingStar(float x, float y, float z, float size, double angle) {
        if (starSweeper == null) starSweeper = new StarSweeper(x, y, z, size, angle);
    }

    public int getColor() {
        float f = Math.max(5.0f - Mth.abs(time-5.0f), 0.0f);
        return 0xffffff | ((int)(f * 255) << 24);
    }

    public double getOffset() {
        float frameTime = (float)time + minecraft.getFrameTime();
        return (frameTime-5.0)*20.0;
    }

    public double getTailOffset() {
        float frameTime = (float)time + minecraft.getFrameTime();
        return (frameTime*frameTime*frameTime-500)*0.2;
    }

    public static void tick(ClientLevel level) {
        if (starSweeper == null) return;
        starSweeper.time++;
        if (starSweeper.time >= 10) starSweeper = null;
    }
}
