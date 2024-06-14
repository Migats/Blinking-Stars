package net.migats21.blink.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.migats21.blink.network.BiStarBlinkPacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import org.apache.commons.compress.utils.Lists;
import org.joml.Vector3f;

import java.util.List;

@Environment(EnvType.CLIENT)
public class StarBlinker {
    public static final Minecraft minecraft = Minecraft.getInstance();
    private static volatile List<StarBlinker> hardBlinkers;
    private final Vector3f position;
    private int id = -1, ticks;
    private boolean hasBlinked;
    private static KeyMapping blinkKey;
    public static int timePhase = 0;
    private final float sensitivity;

    public StarBlinker(int s, float x, float y, float z) {
        sensitivity = s * 0.1f;
        this.position = new Vector3f(x, y, z).normalize(100.0f);
        this.ticks = 0;
        this.hasBlinked = false;
    }

    public static void popBlink() {
        hardBlinkers.removeIf(starBlinker -> !starBlinker.hasBlinked);
    }

    public static float getStarSize(Vector3f position, int id) {
        float frameTime = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        for(StarBlinker starBlinker : hardBlinkers) {
            if (starBlinker.id == id) {
                starBlinker.hasBlinked = true;
                return 20.0f - (float)starBlinker.ticks - frameTime;
            }
            if (starBlinker.id == -1 && !starBlinker.hasBlinked && starBlinker.isCloseEnough(position)) {
                starBlinker.id = id;
                starBlinker.hasBlinked = true;
                return 20.0f - (float)starBlinker.ticks - frameTime;
            }
        }
        return 0.0f;
    }

    public static boolean anyStars() {
        return !hardBlinkers.isEmpty();
    }

    private boolean isCloseEnough(Vector3f position) {
        return position.sub(this.position, new Vector3f()).lengthSquared() < sensitivity * sensitivity;
    }

    public static void blink(int s, float angleX, float angleY) {
        angleX *= Mth.DEG_TO_RAD;
        angleY *= Mth.DEG_TO_RAD;
        // noinspection DataFlowIssue: Blink can only be called when a singleplayer server is running.
        float angleZ = minecraft.level.getSunAngle(minecraft.getTimer().getGameTimeDeltaPartialTick(false));
        float x = Mth.cos(angleX) * Mth.cos(angleY);
        float y = -Mth.sin(angleX);
        float z = Mth.cos(angleX) * Mth.sin(angleY);
        hardBlinkers.add(new StarBlinker(s, x, y * Mth.cos(angleZ) + z * Mth.sin(angleZ), y * -Mth.sin(angleZ) + z * Mth.cos(angleZ)));
    }

    public static void init() {
        hardBlinkers = Lists.newArrayList();
        ClientTickEvents.START_CLIENT_TICK.register((minecraft) -> {
            // noinspection DataFlowIssue: consumeClick will only be true if a world is running
            if (!blinkKey.consumeClick() || !minecraft.level.dimensionType().hasSkyLight()) return;
            // noinspection DataFlowIssue: The camera entity is always present when a world is running
            float angleX = minecraft.getCameraEntity().getXRot();
            float angleY = minecraft.getCameraEntity().getYRot();
            int blinkSensitivity = ConfigOptions.BLINK_SENSITIVITY.get();
            blink(blinkSensitivity, angleX, angleY);
            if (!BlinkingStarsClient.isOnServer) return;
            BiStarBlinkPacket packet = new BiStarBlinkPacket((byte)blinkSensitivity, angleX, angleY);
            ClientPlayNetworking.getSender().sendPacket(packet);
        });
        blinkKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.blink.star", InputConstants.Type.KEYSYM, -1, "key.categories.misc"));
    }

    public static void reset() {
        hardBlinkers.clear();
    }

    public static float getSoftBlink(int sb) {
        if (sb > timePhase && sb < timePhase + 5) {
            return (5.0f + timePhase + minecraft.getTimer().getGameTimeDeltaPartialTick(false) - sb)*0.1f;
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
