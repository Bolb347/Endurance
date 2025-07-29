package blob.endurance.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At("HEAD"), cancellable = true)
    private void disableViewBobbing(net.minecraft.client.util.math.MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        ci.cancel(); // Cancel the bobView method to disable view bobbing
    }
}