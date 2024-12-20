package net.migats21.blink.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.migats21.blink.BlinkingStars;
import net.migats21.blink.client.BlinkingStarsClient;
import net.migats21.blink.client.ConfigOptions;
import net.migats21.blink.client.FallingStar;
import net.migats21.blink.client.StarBlinker;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(SkyRenderer.class)
public abstract class MixinStarRenderer {
    @Shadow private VertexBuffer starBuffer;

    @Shadow protected abstract MeshData drawStars(Tesselator tesselator);

    @Shadow @Final private static ResourceLocation SUN_LOCATION;
    @Unique
    private static final ResourceLocation CURSED_SUN = ResourceLocation.fromNamespaceAndPath(BlinkingStars.MODID, "textures/environment/sun.png");
    @Unique
    private static final int[] STAR_COLORS = {0xffffcc, 0xffccff, 0xffcccc, 0xccffcc, 0xffffff};

    @Inject(method = "renderStars", at = @At("HEAD"))
    private void renderBlinkingStar(FogParameters fogParameters, float f, PoseStack poseStack, CallbackInfo ci) {
        CompiledShaderProgram shaderInstance;
        if (ConfigOptions.ANIMATE_STARS.get() || StarBlinker.anyStars() || FallingStar.getInstance() != null) {
            shaderInstance = RenderSystem.getShader();
            starBuffer.bind();
            starBuffer.upload(drawStars(Tesselator.getInstance()));
            VertexBuffer.unbind();
            RenderSystem.setShader(shaderInstance);
            BlinkingStarsClient.shouldUpdateStars = true;
        } else if (BlinkingStarsClient.shouldUpdateStars) {
            shaderInstance = RenderSystem.getShader();
            starBuffer.bind();
            starBuffer.upload(drawStars(Tesselator.getInstance()));
            VertexBuffer.unbind();
            RenderSystem.setShader(shaderInstance);
            BlinkingStarsClient.shouldUpdateStars = false;
        }
    }

    @Redirect(method = "renderSun", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/SkyRenderer;SUN_LOCATION:Lnet/minecraft/resources/ResourceLocation;"))
    private ResourceLocation changeSunLocation() {
        return BlinkingStarsClient.cursed && ConfigOptions.CURSED_SUNCOLOR.get() ? CURSED_SUN : SUN_LOCATION;
    }

    @ModifyArgs(method = "renderStars", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", ordinal = 0))
    private void changeShaderColor(Args args) {
        if (ConfigOptions.STAR_VARIETY.get()) {
            args.set(0, 1.0f);
            args.set(1, 1.0f);
            args.set(2, 1.0f);
        }
    }

    @Redirect(method = "renderStars", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/CoreShaders;POSITION:Lnet/minecraft/client/renderer/ShaderProgram;"))
    private ShaderProgram changeRenderShader() {
        return CoreShaders.POSITION_COLOR;
    }

    @Inject(method = "drawStars", at = @At("HEAD"), cancellable = true)
    private void drawStars(Tesselator tesselator, CallbackInfoReturnable<MeshData> cir) {
        boolean coloredStars = ConfigOptions.COLORED_STARS.get();
        boolean starVariety = ConfigOptions.STAR_VARIETY.get();
        int d = ConfigOptions.STAR_DENSITY.get().getAsInt();
        float s = ConfigOptions.STAR_SIZE.get().getAsFloat();
        RandomSource randomSource = RandomSource.create(10842L);
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for(int i = 0; i < d; ++i) {
            float x = randomSource.nextFloat() * 2.0f - 1.0f;
            float y = randomSource.nextFloat() * 2.0f - 1.0f;
            float z = randomSource.nextFloat() * 2.0f - 1.0f;
            float w = s + randomSource.nextFloat() * 0.1f;
            float m = Mth.lengthSquared(x, y, z);
            int c = STAR_COLORS[coloredStars ? Math.min(randomSource.nextInt(16), 4) : 4] | (starVariety ? randomSource.nextInt(224) : 224) << 24;
            int b = randomSource.nextInt(1000);
            if (!(m <= 0.010000001F) && !(m >= 1.0f))
                this.drawStar(bufferBuilder, randomSource, new Vector3f(x, y, z).normalize(100.0f), w, b, c, i);
        }

        this.drawStar(bufferBuilder, null, new Vector3f(-100.0f, 0.0f, 0.0f), 0.25f, 0, -1, 16383);
        StarBlinker.popBlink();
        FallingStar fallingStar = FallingStar.getInstance();
        if (fallingStar != null) {
            this.drawFallingStar(bufferBuilder, new Vector3f(fallingStar.position), fallingStar.size, fallingStar.angle, fallingStar.getOffset(), fallingStar.getTailOffset(), fallingStar.getColor());
        }

        cir.setReturnValue(bufferBuilder.buildOrThrow());
        cir.cancel();
    }

    @Unique
    private void drawStar(BufferBuilder bufferBuilder, @Nullable RandomSource randomSource, Vector3f position, float w, int s, int c, int i) {
        float b = StarBlinker.getStarSize(position, i);
        float a = Mth.sqrt(b) / 6.0f;
        if (randomSource != null) {
            a += randomSource.nextFloat() * Mth.PI * 2.0f;
        }
        Quaternionf rotation = (new Quaternionf()).rotateTo(new Vector3f(0.0f, 0.0f, -1.0f), position).rotateZ(a);

        int o = c >>> 24;
        if (ConfigOptions.ANIMATE_STARS.get()) {
            o += (int)((255.0f - (float)o) * Math.max(b * 0.05f, StarBlinker.getSoftBlink(s)));
            c = c & 16777215 | o << 24;
        }
        if (b > 0.0f) {
            b += 1.0f;

            bufferBuilder.addVertex(new Vector3f(w * b, -w * b, 0.0f).rotate(rotation).add(position)).setColor(c);
            bufferBuilder.addVertex(new Vector3f(w, w, 0.0f).rotate(rotation).add(position)).setColor(c);
            bufferBuilder.addVertex(new Vector3f(-w * b, w * b, 0.0f).rotate(rotation).add(position)).setColor(c);
            bufferBuilder.addVertex(new Vector3f(-w, -w, 0.0f).rotate(rotation).add(position)).setColor(c);

            bufferBuilder.addVertex(new Vector3f(w, -w, 0.0f).rotate(rotation).add(position)).setColor(c);
            bufferBuilder.addVertex(new Vector3f(w * b, w * b, 0.0f).rotate(rotation).add(position)).setColor(c);
            bufferBuilder.addVertex(new Vector3f(-w, w, 0.0f).rotate(rotation).add(position)).setColor(c);
            bufferBuilder.addVertex(new Vector3f(-w * b, -w * b, 0.0f).rotate(rotation).add(position)).setColor(c);
        } else {
            bufferBuilder.addVertex(new Vector3f(w, -w, 0.0f).rotate(rotation).add(position)).setColor(c);
            bufferBuilder.addVertex(new Vector3f(w, w, 0.0f).rotate(rotation).add(position)).setColor(c);
            bufferBuilder.addVertex(new Vector3f(-w, w, 0.0f).rotate(rotation).add(position)).setColor(c);
            bufferBuilder.addVertex(new Vector3f(-w, -w, 0.0f).rotate(rotation).add(position)).setColor(c);
        }
    }

    @Unique
    private void drawFallingStar(BufferBuilder bufferBuilder, Vector3f position, float w, float s, float o, float t, int c) {
        Quaternionf rotation = (new Quaternionf()).rotateTo(new Vector3f(0.0f, 0.0f, -1.0f), position).rotateZ(s);
        bufferBuilder.addVertex(new Vector3f(t-w, t-w, 0.0f).rotate(rotation).add(position)).setColor(c);
        bufferBuilder.addVertex(new Vector3f(o+w, o-w, 0.0f).rotate(rotation).add(position)).setColor(c);
        bufferBuilder.addVertex(new Vector3f(o+w, o+w, 0.0f).rotate(rotation).add(position)).setColor(c);
        bufferBuilder.addVertex(new Vector3f(o-w, o+w, 0.0f).rotate(rotation).add(position)).setColor(c);
    }
}
