package blob.endurance;

import blob.endurance.Block.ControllerBlock;
import blob.endurance.Block.ModBlocks;
import blob.endurance.Item.ModItems;
import blob.endurance.entities.ControllerEntity;
import blob.endurance.entities.ModEntities;
import blob.endurance.render.Planet;
import blob.endurance.render.PlanetInspector;
import blob.endurance.vs.ControllerAttachment;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.HashMap;
import java.util.Map;

public class Endurance implements ModInitializer {
	public static final String MOD_ID = "endurance";
	public static final PlanetInspector INSPECTOR = new PlanetInspector();

	public static final Map<BlockPos, ControllerEntity> CONTROLLERS = new HashMap<>();

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

		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			// Get block position
			BlockPos pos = hit.getBlockPos();
			ControllerAttachment attachment;

			if (player.isSneaking()) {
				return ActionResult.PASS;
			}

			// Check if it's your ControllerBlock
			if (world.getBlockState(pos).isOf(ModBlocks.CONTROLLER_BLOCK)) {
				if (!world.isClient) {
					for (ItemStack item : player.getHandItems()) {
						if (item.isOf(ModItems.SHIP_DESIGNATOR)) {
							return ActionResult.PASS;
						}
					}
					ControllerEntity entity = Endurance.CONTROLLERS.get(pos);
					if (entity == null) {
						Direction dir = world.getBlockState(pos).get(Properties.HORIZONTAL_FACING);
						if (!world.isClient) {
							LoadedServerShip owner = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
							if (owner != null) {
								ControllerEntity _entity = new ControllerEntity(ModEntities.CONTROLLER_ENTITY, world);
								_entity.setPosition(pos.toCenterPos());
								_entity.setYaw(dir.asRotation());
								_entity.bind(owner);
								if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
									world.spawnEntity(_entity);
									Endurance.CONTROLLERS.put(pos, _entity);
								} else {
									_entity.discard();
								}
								attachment = owner.getAttachment(ControllerAttachment.class);
								if (attachment == null) {
									owner.saveAttachment(ControllerAttachment.class, new ControllerAttachment());
									attachment = owner.getAttachment(ControllerAttachment.class);
								}
								if (attachment != null) {
									attachment.attachController(pos, dir);
								}
							}
						}
					}

					entity = Endurance.CONTROLLERS.get(pos);
					if (entity != null) {
						if (!player.isUsingItem()) {
							player.startRiding(entity);
							return ActionResult.SUCCESS;
						}
					}
				}
			}

			return ActionResult.PASS; // let vanilla handle everything else
		});
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
	}
}