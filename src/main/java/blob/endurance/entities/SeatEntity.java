package blob.endurance.entities;

import blob.endurance.Endurance;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class SeatEntity extends Entity {
    private final static float acceleration = 5f;
    private final static float maxSpeed = 100f;
    private final static float damping = 0.9f;

    public SeatEntity(EntityType<? extends SeatEntity> type, World world) {
        super(type, world);
        this.noClip = false; // Make sure the entity is collidable
        this.setNoGravity(true); // If you don't want gravity, optional
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
        DataTracker dt = this.dataTracker;
        super.tick();

        if (hasPassengers()) {
            Entity passenger = getFirstPassenger();

            if (passenger instanceof PlayerEntity player) {
                handlePlayerInput(player);
            }
        }

        Endurance.shipPrevPos = Endurance.shipPos;
        Endurance.shipVel.mul(damping);
        Endurance.shipPos.add(Endurance.shipVel);
    }

    private void handlePlayerInput(PlayerEntity player) {
        if (player instanceof ClientPlayerEntity clientPlayer) {
            boolean forward = clientPlayer.input.pressingForward;
            boolean back = clientPlayer.input.pressingBack;
            boolean left = clientPlayer.input.pressingLeft;
            boolean right = clientPlayer.input.pressingRight;

            float pitch = Endurance.shipRot.x;
            float yaw = Endurance.shipRot.y;
            float roll = Endurance.shipRot.z;

            Matrix3f rotation = new Matrix3f()
                    .rotationY(yaw)
                    .rotateX(pitch)
                    .rotateZ(roll);

            Vector3f rightVec = new Vector3f();   // X axis
            Vector3f upVec = new Vector3f();      // Y axis
            Vector3f forwardVec = new Vector3f(); // Z axis

            rotation.getColumn(0, rightVec);     // Local right
            rotation.getColumn(1, upVec);        // Local up
            rotation.getColumn(2, forwardVec);   // Local forward

            // Step 3: Compute movement vector
            Vector3f accelVec = new Vector3f();
            if (forward) accelVec.add(forwardVec);
            if (back) accelVec.sub(forwardVec);
            if (left) accelVec.add(rightVec);
            if (right) accelVec.sub(rightVec);

            if (accelVec.lengthSquared() > 0) {
                accelVec.normalize().mul(acceleration);
                accelVec.mul(-1);
                accelVec.y *= -1;
                Endurance.shipVel.add(accelVec);

                if (Endurance.shipVel.length() > maxSpeed) {
                    Endurance.shipVel.normalize().mul(maxSpeed);
                }
            }
        }
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(0.75f, 0.5f); // size of seat
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
