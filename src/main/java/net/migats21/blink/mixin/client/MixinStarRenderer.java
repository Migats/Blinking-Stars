package net.migats21.blink.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.BlinkingStarsClient;
import net.migats21.blink.client.ConfigOptions;
import net.migats21.blink.client.StarBlinker;
import net.migats21.blink.client.FallingStar;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Supplier;

@Mixin(LevelRenderer.class)
public abstract class MixinStarRenderer {
    @Unique
    private static final ResourceLocation CURSED_SUN = new ResourceLocation(BlinkingStars.MODID, "textures/environment/sun.png");
    @Unique
    private static final int[] STAR_COLORS = {0xffffcc, 0xffccff, 0xffcccc, 0xccffcc, 0xffffff};

    @Shadow protected abstract void createStars();

    @Inject(method = "renderSky", at = @At("HEAD"))
    private void renderBlinkingStar(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        ShaderInstance shaderInstance;
        if (ConfigOptions.ANIMATE_STARS.get() || StarBlinker.anyStars() || FallingStar.getInstance() != null) {
            shaderInstance = RenderSystem.getShader();
            this.createStars();
            RenderSystem.setShader(() -> shaderInstance);
            BlinkingStarsClient.shouldUpdateStars = true;
        } else if (BlinkingStarsClient.shouldUpdateStars) {
            shaderInstance = RenderSystem.getShader();
            this.createStars();
            RenderSystem.setShader(() -> shaderInstance);
            BlinkingStarsClient.shouldUpdateStars = false;
        }
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0), index = 1)
    private ResourceLocation changeSunLocation(ResourceLocation normalSun) {
        return BlinkingStarsClient.cursed && ConfigOptions.CURSED_SUNCOLOR.get() ? CURSED_SUN : normalSun;
    }

    @ModifyArgs(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 3))
    private void changeShaderColor(Args args) {
        if (ConfigOptions.STAR_VARIETY.get()) {
            args.set(0, 1.0f);
            args.set(1, 1.0f);
            args.set(2, 1.0f);
        }
    }

    @ModifyArg(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V", ordinal = 1), index = 2)
    private ShaderInstance changeRenderShader(ShaderInstance posShader) {
        return GameRenderer.getPositionColorShader();
    }

    @ModifyArg(method = "createStars", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V"), index = 0)
    private Supplier<ShaderInstance> changeWriterShader(Supplier<ShaderInstance> posShader) {
        return GameRenderer::getPositionColorShader;
    }

    @Inject(method = "drawStars", at = @At("HEAD"), cancellable = true)
    private void drawStars(BufferBuilder bufferBuilder, CallbackInfoReturnable<BufferBuilder.RenderedBuffer> cir) {
        boolean coloredStars = ConfigOptions.COLORED_STARS.get();
        boolean starVariety = ConfigOptions.STAR_VARIETY.get();
        int density = ConfigOptions.STAR_DENSITY.get().getAsInt();
        float size = ConfigOptions.STAR_SIZE.get().getAsFloat();
        RandomSource randomSource = RandomSource.create(10842L);
        RandomSource randomSource1 = RandomSource.create(44654L);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for(int i = 0; i < density; ++i) {
            double d = randomSource.nextFloat() * 2.0f - 1.0f;
            double e = randomSource.nextFloat() * 2.0f - 1.0f;
            double f = randomSource.nextFloat() * 2.0f - 1.0f;
            double g = size + randomSource.nextFloat() * 0.1f;
            int ii = STAR_COLORS[coloredStars ? Math.min(randomSource1.nextInt(16), 4) : 4] | (starVariety ? randomSource1.nextInt(224) : 224) << 24;
            int sb = randomSource1.nextInt(1000);
            this.drawStar(bufferBuilder, randomSource, d, e, f, g, sb, ii, i);
        }

        this.drawStar(bufferBuilder, null, -0.5, 0.0, 0.0, 0.25, 0, -1, 3000);
        StarBlinker.popBlink();
        FallingStar fallingStar = FallingStar.getInstance();
        if (fallingStar != null) {
            this.drawFallingStar(bufferBuilder, fallingStar.x, fallingStar.y, fallingStar.z, fallingStar.size, fallingStar.angle, fallingStar.getOffset(), fallingStar.getTailOffset(), fallingStar.getColor());
        }

        cir.setReturnValue(bufferBuilder.end());
        cir.cancel();
    }

    @Unique
    private void drawStar(BufferBuilder bufferBuilder, RandomSource randomSource, double d, double e, double f, double g, int sb, int ii, int id) {
        double h = d * d + e * e + f * f;
        if (h < 1.0 && h > 0.01) {
            h = 1.0 / Math.sqrt(h);
            d *= h;
            e *= h;
            f *= h;
            double j = d * 100.0;
            double k = e * 100.0;
            double l = f * 100.0;
            double m = Math.atan2(d, f);
            double n = Math.sin(m);
            double o = Math.cos(m);
            double p = Math.atan2(Math.sqrt(d * d + f * f), e);
            double q = Math.sin(p);
            double r = Math.cos(p);
            double bs = StarBlinker.getStarSize(d, e, f, id);
            double sp = 4.0;
            if (bs > 0.0) {
                sp = 8.0;
            }

            double s = Math.sqrt(bs) / 6.0;
            if (randomSource != null) {
                s += randomSource.nextDouble() * Math.PI * 2.0;
            }

            double t = Math.sin(s);
            double u = Math.cos(s);
            int ca = ii >>> 24;
            if (ConfigOptions.ANIMATE_STARS.get()) {
                ca += (int)((255.0f - (float)ca) * Math.max((float)bs / 20.0f, StarBlinker.getSoftBlink(sb)));
                ii = ii & 16777215 | ca << 24;
            }

            for(int v = 0; (double)v < sp; ++v) {
                double w = (double)(v & 1 ^ v >> 2 & 1) * bs + 1.0;
                double x = (double)((v & 2) - 1) * g * w;
                double y = (double)((v + 1 & 2) - 1) * g * w;
                double z = 0.0;
                double aa = x * u - y * t;
                double ab = y * u + x * t;
                double ad = aa * q + 0.0 * r;
                double ae = 0.0 * q - aa * r;
                double af = ae * n - ab * o;
                double ah = ab * n + ae * o;
                bufferBuilder.vertex(j + af, k + ad, l + ah).color(ii).endVertex();
            }
        }
    }

    @Unique
    private void drawFallingStar(BufferBuilder bufferBuilder, double d, double e, double f, double g, double s, double offset, double tailOffset, int ii) {
        double j = d * 100.0;
        double k = e * 100.0;
        double l = f * 100.0;
        double m = Math.atan2(d, f);
        double n = Math.sin(m);
        double o = Math.cos(m);
        double p = Math.atan2(Math.sqrt(d * d + f * f), e);
        double q = Math.sin(p);
        double r = Math.cos(p);
        double t = Math.sin(s);
        double u = Math.cos(s);
        for(int v = 0; v < 4; ++v) {
            double x = (double)((v & 2) - 1) * g;
            double y = (double)((v + 1 & 2) - 1) * g;
            if (v == 0) {
                x += tailOffset;
                y += tailOffset;
            } else {
                x += offset;
                y += offset;
            }
            double z = 0.0;
            double aa = x * u - y * t;
            double ab = y * u + x * t;
            double ad = aa * q + 0.0 * r;
            double ae = 0.0 * q - aa * r;
            double af = ae * n - ab * o;
            double ah = ab * n + ae * o;
            bufferBuilder.vertex(j + af, k + ad, l + ah).color(ii).endVertex();
        }
    }
}
