package blob.endurance.mixin;

import blob.endurance.dimension.ModDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class EntityMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onVoidDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (entity instanceof PlayerEntity player) {
            if (source.isOf(DamageTypes.OUT_OF_WORLD) && player.getWorld().getRegistryKey().equals(ModDimensions.SPACE_DIMENSION_KEY)) {
                // Cancel void damage
                cir.setReturnValue(false);
            }
        }
    }
}