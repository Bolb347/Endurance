package blob.endurance;

import blob.endurance.dimension.ModDimensions;
import blob.endurance.entities.ModEntities;
import blob.endurance.entities.SeatEntity;
import blob.endurance.entities.SeatEntityRenderer;
import blob.endurance.render.Planet;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector3f;


public class EnduranceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModDimensions.registerModDimensions();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            InputHandler.updateRotation();
            Planet.atmoColor.computeAtmoColors();
        });
        EntityRendererRegistry.register(ModEntities.SEAT_ENTITY, SeatEntityRenderer::new);
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, deltaTracker, camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_WEATHER) {
                if (MinecraftClient.getInstance().world.getRegistryKey().equals(ModDimensions.SPACE_DIMENSION_KEY)) {
                    Planet.render(
                            new Vector3f[]{
                                    new Vector3f(0, 400, 0),
                                    new Vector3f(1000, 400, 0),
                                    new Vector3f(2000, 400, 0),
                                    new Vector3f(3000, 400, 0),
                                    new Vector3f(4500, 400, 0),
                                    new Vector3f(9500, 400, 0),
                                    new Vector3f(12000, 400, 0),
                                    new Vector3f(18000, 400, 0),
                                    new Vector3f(22000, 400, 0),
                                    new Vector3f(25000, 400, 0)
                            },
                            new float[]{250, 49, 121, 127, 68, 630, 505, 311, 295, 12},
                            new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            new float[]{0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f},
                            new float[]{0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f},
                            new Vector3f(0, 400, 0)
                    );
                }
            }
        });
    }
}