package net.migats21.blink.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class StarBlinker {
    public static final Minecraft minecraft = Minecraft.getInstance();
    private static volatile List<StarBlinker> hardBlinkers;
    private static boolean isServerAllowed = false;
    private final double x, y, z;
    private int id = -1, ticks;
    private boolean hasBlinked;
    private static KeyMapping blinkKey;
    public static int timePhase = 0;

    public StarBlinker(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ticks = 0;
        this.hasBlinked = false;
    }

    public static void popBlink() {
        hardBlinkers.removeIf(starBlinker -> !starBlinker.hasBlinked);
    }

    public static double getStarSize(double x, double y, double z, int id) {
        for(StarBlinker starBlinker : hardBlinkers) {
            if (starBlinker.id == id) {
                starBlinker.hasBlinked = true;
                return 20.0f - (float)starBlinker.ticks - minecraft.getFrameTime();
            }
            if (starBlinker.id == -1 && !starBlinker.hasBlinked && starBlinker.isCloseEnough(x, y, z)) {
                starBlinker.id = id;
                starBlinker.hasBlinked = true;
                return 20.0f - (float)starBlinker.ticks - minecraft.getFrameTime();
            }
        }
        return 0.0;
    }

    private boolean isCloseEnough(double x, double y, double z) {
        return Math.abs(x - this.x) < 0.01 && Math.abs(y - this.y) < 0.01 && Math.abs(z - this.z) < 0.01;
    }
    @Deprecated(forRemoval = true)
    public static void updateFrameTime() {
        float deltaFrameTime = minecraft.getDeltaFrameTime();
        timePhase += deltaFrameTime;
        if (timePhase >= 1000.0f) timePhase -= 1000.0f;
        for(StarBlinker starBlinker : hardBlinkers) {
            starBlinker.hasBlinked = false;
            starBlinker.ticks -= deltaFrameTime;
        }
        hardBlinkers.removeIf(starBlinker -> starBlinker.ticks <= 0.0);
    }
    public static void blink(Minecraft minecraft, double angleX, double angleY) {
        double angleZ = minecraft.level.getSunAngle(minecraft.getFrameTime());
        double x = Math.cos(angleX) * Math.cos(angleY);
        double y = -Math.sin(angleX);
        double z = Math.cos(angleX) * Math.sin(angleY);
        hardBlinkers.add(new StarBlinker(x, y * Math.cos(angleZ) + z * Math.sin(angleZ), y * -Math.sin(angleZ) + z * Math.cos(angleZ)));
    }
    public static void init() {
        hardBlinkers = new ArrayList<>();
        ClientTickEvents.START_CLIENT_TICK.register((minecraft) -> {
            if (blinkKey.consumeClick() && minecraft.level.dimensionType().hasSkyLight()) {
                if (isServerAllowed) {
                    minecraft.player.connection.sendCommand("blink");
                } else {
                    blink(minecraft, minecraft.getCameraEntity().getXRot() * Mth.DEG_TO_RAD, minecraft.getCameraEntity().getYRot() * Mth.DEG_TO_RAD);
                }
            }
        });
        blinkKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.blink.star", InputConstants.Type.KEYSYM, -1, "key.categories.misc"));
    }

    public static void isServerAllowed() {
        isServerAllowed = true;
    }

    public static void reset() {
        hardBlinkers.clear();
        isServerAllowed = false;
    }

    public static float getSoftBlink(int sb) {
        if (sb > timePhase && sb < timePhase + 5) {
            return (5.0f + timePhase + minecraft.getFrameTime() - sb)*0.1f;
        }
        return 0.0f;
    }

    public static void tick(ClientLevel level) {
        timePhase = (int)(level.getGameTime() % 1000);
        for(StarBlinker starBlinker : hardBlinkers) {
            starBlinker.hasBlinked = false;
            starBlinker.ticks++;
        }
        hardBlinkers.removeIf(starBlinker -> starBlinker.ticks >= 20);
    }
}