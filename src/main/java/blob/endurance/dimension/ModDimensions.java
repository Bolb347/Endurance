package blob.endurance.dimension;

import blob.endurance.Endurance;
import blob.endurance.EnduranceClient;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import static blob.endurance.Endurance.MOD_ID;

public class ModDimensions {
    public static final RegistryKey<World> SPACE_DIMENSION_KEY = RegistryKey.of(
            RegistryKeys.WORLD,
            Identifier.of(MOD_ID, "space_dim")
    );

    public static final RegistryKey<DimensionType> SPACE_DIMENSION_TYPE_KEY = RegistryKey.of(
            RegistryKeys.DIMENSION_TYPE,
            Identifier.of(MOD_ID, "space_dim")
    );

    public static void registerModDimensions(){
        Endurance.LOGGER.info("Registering dimensions");
        DimensionRenderingRegistry.registerDimensionEffects(
                Identifier.of(MOD_ID, "space_dim"),
                new SpaceDimensionEffects()
        );
        DimensionRenderingRegistry.registerSkyRenderer(
                ModDimensions.SPACE_DIMENSION_KEY,
                (context) -> {
                    // no-op to disable sky rendering (clouds, sun, moon, stars)
                }
        );
    }
}