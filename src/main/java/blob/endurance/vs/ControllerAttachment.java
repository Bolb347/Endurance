package blob.endurance.vs;

import blob.endurance.Endurance;
import blob.endurance.entities.ControllerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.ArrayList;

public class ControllerAttachment implements ShipForcesInducer {
    private ArrayList<BlockPos> m_seats = new ArrayList<>();
    private ArrayList<Direction> m_seats_rotations = new ArrayList<>();

    @Override
    public void applyForces(@NotNull PhysShip ship) {
        for (int i = 0; i < m_seats.size(); i ++ ) {
            applyControllerForce(ship, i);
        }
    }

    private Vector3f getVector(int index) {
        for (Entity entity : MinecraftClient.getInstance().world.getOtherEntities(null, new Box(m_seats.get(index)).expand(0.5))) {
            if (entity instanceof ControllerEntity controller) {
                return controller.m_relMovementVector;
            }
        }
        return new Vector3f(0);
    }

    private void applyControllerForce(PhysShip ship, int index) {
        Vector3f relForceDir = getVector(index);

        Vector3d forceDir = new Vector3d(relForceDir);

        // We transform the blocks direction from absolute (e.g. 1, 0, 0) to ship rotated (e.g. 0.87, 0.22, 0.53)
        forceDir = ship.getTransform().getShipToWorld().transformDirection(forceDir, new Vector3d());
        // Make it bigggg
        forceDir.mul(Endurance.THRUSTER_POWER);

        Vector3d pos = VectorConversionsMCKt.toJOML(m_seats.get(index).toCenterPos());

        // JOML updates the original variable when methods are called on it
        // getPositionInShip is what we need to get it "relative to center of mass"
        // Weirdly enough, using the actual center of mass value doesn't get the right offset
        pos.sub(ship.getTransform().getPositionInShip());

        // RotDependentToPos is broken, so we manually transform forceDir earlier and apply it Invariant-ly
        ship.applyInvariantForceToPos(forceDir, pos);
    }

    public void attachController(BlockPos pos, Direction rotation) {
        m_seats.add(pos);
        m_seats_rotations.add(rotation);
    }

    public void removeController(BlockPos pos) {
        for (int i = 0; i < m_seats.size(); i ++) {
            if (pos == m_seats.get(i)) {
                m_seats.remove(i);
                i --;
            }
        }
    }
}
