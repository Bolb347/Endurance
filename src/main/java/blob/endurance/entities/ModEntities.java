package blob.endurance.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<ControllerEntity> CONTROLLER_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of("endurance", "seat_entity"),
            EntityType.Builder.<ControllerEntity>create(ControllerEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f)
                    .trackingTickInterval(10)
                    .build()
    );

    public static void register() {

    }
}