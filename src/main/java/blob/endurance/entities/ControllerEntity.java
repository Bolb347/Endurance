package blob.endurance.entities;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.LoadedServerShip;

public class ControllerEntity extends Entity {
    private final static float acceleration = 5f;
    private final static float maxSpeed = 100f;
    private final static float damping = 0.9f;

    public Vector3f m_relMovementVector;
    public LoadedServerShip m_parent;

    public ControllerEntity(EntityType<? extends ControllerEntity> type, World world) {
        super(type, world);
        this.noClip = false; // Make sure the entity is collidable
        this.setNoGravity(true); // If you don't want gravity, optional
    }

    public void bind(LoadedServerShip ship) {
        m_parent = ship;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // Load any custom data here
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // Save any custom data here
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public boolean isPushable() {
        return true; // Even if it's static, this helps make it interactable
    }

    @Override
    public boolean shouldSave() {
        return true; // Ensure it stays in the world
    }

    @Override
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!getWorld().isClient) {
            player.startRiding(this);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void tick() {
        super.tick();

        if (hasPassengers()) {
            Entity passenger = getFirstPassenger();

            if (passenger instanceof PlayerEntity player) {
                handlePlayerInput(player);
            }
        }
    }

    private void handlePlayerInput(PlayerEntity player) {
        if (player instanceof ClientPlayerEntity clientPlayer) {
            boolean forward = clientPlayer.input.pressingForward;
            boolean back = clientPlayer.input.pressingBack;
            boolean left = clientPlayer.input.pressingLeft;
            boolean right = clientPlayer.input.pressingRight;

            float x = 0;
            float z = 0;

            if (forward) z += 1f;
            if (back)    z -= 1f;
            if (left)    x += 1f;
            if (right)   x -= 1f;

            m_relMovementVector = new Vector3f(x, 0, z);

            if (x == 0 && z == 0) {
                return;
            }

            float yaw = getYaw();
            double radians = Math.toRadians(-yaw);


            m_relMovementVector.rotateY((float) radians);
            m_relMovementVector.normalize();
        }
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(1f, 1f); // size of seat
    }

    @Override
    public Box getVisibilityBoundingBox() {
        return this.getBoundingBox().expand(0.1f); // expand slightly to make interaction easier
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isAlive() {
        return true;
    }
}
