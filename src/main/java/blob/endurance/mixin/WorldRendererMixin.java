package blob.endurance.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static blob.endurance.dimension.ModDimensions.SPACE_DIMENSION_KEY;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FDDD)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onRenderClouds(MatrixStack matrices, Matrix4f matrix4f, Matrix4f matrix4f2, float tickDelta,
                                double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {

        // Get current dimension key
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        RegistryKey<World> currentDim = client.world.getRegistryKey();

        // Check if current dimension is your custom space dimension
        if (currentDim.equals(SPACE_DIMENSION_KEY)) {
            // Cancel cloud rendering
            ci.cancel();
        }
    }
}