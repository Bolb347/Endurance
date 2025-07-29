package blob.endurance.render;

import blob.endurance.Endurance;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.shader.uniform.ShaderUniformAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;


public class Planet {

    private static final Identifier PLANET_POST = Identifier.of("endurance", "planetpost");

    public static void render(Vector3f[] worldpos, float[] radius, float[] rotation, float[] aurastrength, float[] aurafalloff, Vector3f sunpos) {
        PostPipeline pipeline = VeilRenderSystem.renderer().getPostProcessingManager().getPipeline(PLANET_POST);
        Vector3f lerpedPos = new Vector3f(Endurance.shipPrevPos).lerp(Endurance.shipPos, MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false));
        if (pipeline != null) {
            Vector3f[] translatedPos = new Vector3f[worldpos.length];
            // Create a translated copy of worldpos by adding shipPos to each Vector3f
            for (int i = 0; i < worldpos.length; i++) {
                translatedPos[i] = new Vector3f(worldpos[i]).add(lerpedPos);
            }

            ShaderUniformAccess uniform = pipeline.getOrCreateUniform("WorldPos");
            uniform.setVectors(translatedPos);

            uniform = pipeline.getOrCreateUniform("Radius");
            uniform.setFloats(radius);

            uniform = pipeline.getOrCreateUniform("Rotation");
            uniform.setFloats(rotation);

            uniform = pipeline.getOrCreateUniform("AuraStength");
            uniform.setFloats(aurastrength);

            uniform = pipeline.getOrCreateUniform("AuraFalloff");
            uniform.setFloats(aurafalloff);

            uniform = pipeline.getOrCreateUniform("ScreenSize");
            uniform.setVector(MinecraftClient.getInstance().getFramebuffer().textureWidth, MinecraftClient.getInstance().getFramebuffer().textureHeight);

            uniform = pipeline.getOrCreateUniform("SunPos");
            uniform.setVector(sunpos);

            uniform = pipeline.getOrCreateUniform("ShipRot");
            uniform.setVector(Endurance.shipRot);
        }
        VeilRenderSystem.renderer().getPostProcessingManager().runPipeline(pipeline);
    }
}