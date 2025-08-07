package blob.endurance;

import blob.endurance.Block.ModBlocks;
import blob.endurance.Item.ModItems;
import blob.endurance.entities.ModEntities;
import blob.endurance.render.Planet;
import blob.endurance.render.PlanetInspector;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Endurance implements ModInitializer {
	public static final String MOD_ID = "endurance";
	public static final PlanetInspector INSPECTOR = new PlanetInspector();

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static float THRUSTER_POWER = 40000;
	public static BlockPos INV_POS = new BlockPos(0, -10000, 0);

	public static Vector3f[] PLANET_POSES = new Vector3f[]{
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
	};

	public static float[] PLANET_RADII = new float[]{250, 49, 121, 127, 68, 630, 505, 311, 295, 12};
	public static float[] PLANET_ROTATIONS = new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	public static float[] PLANET_AURA_STRENGTH = new float[]{0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};
	public static float[] PLANET_AURA_FALLOFF = new float[]{0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};
	public static Vector3f SUN_POS = new Vector3f(0, 400, 0);

	@Override
	public void onInitialize() {
		ModEntities.register();
		ModBlocks.register();
		ModItems.register();
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}
}