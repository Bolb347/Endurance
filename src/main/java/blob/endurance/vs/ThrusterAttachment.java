package blob.endurance.vs;

import blob.endurance.Endurance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.core.api.ships.ShipForcesInducer;

import java.util.ArrayList;

public class ThrusterAttachment implements ShipForcesInducer {
    private ArrayList<BlockPos> m_thrusters = new ArrayList<>();
    private ArrayList<Direction> m_thruster_rotations = new ArrayList<>();

    @Override
    public void applyForces(@NotNull PhysShip ship) {
        for (int i = 0; i < m_thrusters.size(); i ++ ) {
            Vector3f forceDir = new Vector3f(0, 0, 1);
            Direction blockRot = m_thruster_rotations.get(i);
            forceDir = blockRot.getUnitVector();
            forceDir.mul(Endurance.THRUSTER_POWER);
            Vec3d thrusterPos = m_thrusters.get(i).toCenterPos();
            ship.applyRotDependentForceToPos(new Vector3d(forceDir), new Vector3d(thrusterPos.x, thrusterPos.y, thrusterPos.z));
        }
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