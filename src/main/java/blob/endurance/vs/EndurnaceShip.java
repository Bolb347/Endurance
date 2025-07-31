package blob.endurance.vs;

import blob.endurance.Block.ThrusterBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.PhysShip;
import org.valkyrienskies.mod.common.assembly.ShipAssembler;

public class EndurnaceShip {
    public Map<BlockPos, ThrusterBlock> thrusters = new ConcurrentHashMap<>();

    ServerShip m_ship;

    void EnduranceShip(BlockPos shipSeat) {
        if (MinecraftClient.getInstance().world == null) {
            return;
        }
        List<BlockPos> blocks = new ArrayList<BlockPos>();
        m_ship = ShipAssembler.INSTANCE.assembleToShip(MinecraftClient.getInstance().world, blocks, true, 1, true);
    }
}
