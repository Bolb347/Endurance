package blob.endurance.entities;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.VertexConsumerProvider;

public class ControllerEntityRenderer extends EntityRenderer<ControllerEntity> {
    public ControllerEntityRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    public void render(ControllerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        // Optionally render something or nothing if invisible.
    }

    @Override
    public Identifier getTexture(ControllerEntity entity) {
        return null; // or return a valid texture if rendering something
    }
}
