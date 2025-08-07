package blob.endurance;

import blob.endurance.dimension.ModDimensions;
import blob.endurance.entities.ModEntities;
import blob.endurance.entities.SeatEntityRenderer;
import blob.endurance.render.Planet;
import blob.endurance.render.PlanetInspector;
import foundry.veil.api.client.editor.Inspector;
import foundry.veil.api.client.render.VeilRenderer;
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
        VeilEventPlatform.INSTANCE.onVeilRendererAvailable((VeilRenderer renderer) -> {
            renderer.getEditorManager().add(Endurance.INSPECTOR);
        });
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack, frustumMatrix, projectionMatrix, renderTick, deltaTracker, camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_WEATHER) {
                if (MinecraftClient.getInstance().world.getRegistryKey().equals(ModDimensions.SPACE_DIMENSION_KEY)) {
                    Planet.render(
                            Endurance.PLANET_POSES,
                            Endurance.PLANET_RADII,
                            Endurance.PLANET_ROTATIONS,
                            Endurance.PLANET_AURA_STRENGTH,
                            Endurance.PLANET_AURA_FALLOFF,
                            Endurance.SUN_POS
                    );
                }
            }
        });
    }
}