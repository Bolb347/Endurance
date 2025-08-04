package blob.endurance.vs;

import blob.endurance.Endurance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ValkyrienSkiesException;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.ArrayList;

public class ThrusterAttachment implements ShipForcesInducer {
    private ArrayList<BlockPos> m_thrusters = new ArrayList<>();
    private ArrayList<Direction> m_thruster_rotations = new ArrayList<>();

    @Override
    public void applyForces(@NotNull PhysShip ship) {
        for (int i = 0; i < m_thrusters.size(); i ++ ) {
            applyThrusterForce(ship, i);
        }
    }

    private void applyThrusterForce(PhysShip ship, int index) {
        Direction blockRot = m_thruster_rotations.get(index);

        Vector3d forceDir = new Vector3d(blockRot.getUnitVector());

        // We transform the blocks direction from absolute (e.g. 1, 0, 0) to ship rotated (e.g. 0.87, 0.22, 0.53)
        forceDir = ship.getTransform().getShipToWorld().transformDirection(forceDir, new Vector3d());
        // Make it bigggg
        forceDir.mul(Endurance.THRUSTER_POWER);

        Vector3d pos = VectorConversionsMCKt.toJOML(m_thrusters.get(index).toCenterPos());

        // JOML updates the original variable when methods are called on it
        // getPositionInShip is what we need to get it "relative to center of mass"
        // Weirdly enough, using the actual center of mass value doesn't get the right offset
        pos.sub(ship.getTransform().getPositionInShip());

        // RotDependentToPos is broken, so we manually transform forceDir earlier and apply it Invariant-ly
        ship.applyInvariantForceToPos(forceDir, pos);
    }

    public void attachThruster(BlockPos pos, Direction rotation) {
        m_thrusters.add(pos);
        m_thruster_rotations.add(rotation);
    }

    public void removeThruster(BlockPos pos) {
        for (int i = 0; i < m_thrusters.size(); i ++) {
            if (pos == m_thrusters.get(i)) {
                m_thrusters.remove(i);
                i --;
            }
        }
    }
}