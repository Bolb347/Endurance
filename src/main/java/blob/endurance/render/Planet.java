package blob.endurance.render;

import blob.endurance.Endurance;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.uniform.ShaderUniformAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;


public class Planet {
    public static AtmoColor atmoColor = new AtmoColor();

    private static final Identifier PLANET_POST = Identifier.of("endurance", "planetpost");

    public static void render(Vector3f[] worldpos, float[] radius, float[] rotation, float[] aurastrength, float[] aurafalloff, Vector3f sunpos) {
        PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(PLANET_POST);
        if (pipeline != null) {
            ShaderUniformAccess uniform = pipeline.getOrCreateUniform("WorldPos");
            uniform.setVectors(worldpos);

            uniform = pipeline.getOrCreateUniform("Radius");
            uniform.setFloats(radius);

            uniform = pipeline.getOrCreateUniform("Rotation");
            uniform.setFloats(rotation);

            uniform = pipeline.getOrCreateUniform("AuraStrength");
            uniform.setFloats(aurastrength);

            uniform = pipeline.getOrCreateUniform("AuraFalloff");
            uniform.setFloats(aurafalloff);

            uniform = pipeline.getOrCreateUniform("ScreenSize");
            uniform.setVector(MinecraftClient.getInstance().getFramebuffer().textureWidth, MinecraftClient.getInstance().getFramebuffer().textureHeight);

            uniform = pipeline.getOrCreateUniform("SunPos");
            uniform.setVector(sunpos);

            uniform = pipeline.getOrCreateUniform("AtmoColors");
            uniform.setVectors(atmoColor.atmoColors);
        }
        VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(pipeline);
    }
}