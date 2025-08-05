package blob.endurance;

import blob.endurance.Block.ModBlocks;
import blob.endurance.Item.ModItems;
import blob.endurance.entities.ModEntities;
import blob.endurance.render.Planet;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Endurance implements ModInitializer {
	public static final String MOD_ID = "endurance";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static float THRUSTER_POWER = 40000;
	public static BlockPos INV_POS = new BlockPos(0, -10000, 0);

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