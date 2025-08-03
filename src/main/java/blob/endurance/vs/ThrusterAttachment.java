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
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
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
        Vector3f forceDir = blockRot.getUnitVector();
        forceDir.mul(Endurance.THRUSTER_POWER);
        Vec3d pos = m_thrusters.get(index).toCenterPos().add((Vec3d) ship.getCenterOfMass());
        Vector3d thrusterPos = new Vector3d(pos.x, pos.y, pos.z);
        ship.applyRotDependentForceToPos(new Vector3d(forceDir), thrusterPos);
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