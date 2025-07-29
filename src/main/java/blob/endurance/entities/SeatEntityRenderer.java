package blob.endurance.entities;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.VertexConsumerProvider;

public class SeatEntityRenderer extends EntityRenderer<SeatEntity> {
    public SeatEntityRenderer(Context ctx) {
        super(ctx);
    }

    @Override
    public void render(SeatEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        // Optionally render something or nothing if invisible.
    }

    @Override
    public Identifier getTexture(SeatEntity entity) {
        return null; // or return a valid texture if rendering something
    }
}
