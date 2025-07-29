package blob.endurance;

import blob.endurance.entities.ModEntities;
import net.fabricmc.api.ModInitializer;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Endurance implements ModInitializer {
	public static final String MOD_ID = "endurance";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Vector3f shipPos = new Vector3f(0, 0, 0);
	public static Vector3f shipPrevPos = new Vector3f(0, 0, 0);
	public static Vector3f shipRot = new Vector3f(0, 0, 0);
	public static Vector3f shipVel = new Vector3f(0, 0, 0);


	@Override
	public void onInitialize() {
		ModEntities.register();
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}
}