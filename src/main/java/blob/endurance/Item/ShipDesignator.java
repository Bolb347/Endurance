package blob.endurance.Item;
import blob.endurance.Block.ModBlocks;
import blob.endurance.Endurance;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.assembly.ShipAssembler;

import java.util.ArrayList;
import java.util.List;

public class ShipDesignator extends Item {
    private ServerShip ship;
    public ShipDesignator(Settings settings) {
        super(settings);
        ship = null;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
            return ActionResult.PASS; // Don't run on client
        }
        BlockPos pos = context.getBlockPos();
        BlockPos pos1 = NbtHelper.getPosition(context.getStack(), "Pos1");
        BlockPos pos2 = NbtHelper.getPosition(context.getStack(), "Pos2");
        if (!(pos1.equals(Endurance.INV_POS) || pos2.equals(Endurance.INV_POS))) {
            if (serverWorld.getBlockState(pos).getBlock() == ModBlocks.CONTROLLER_BLOCK) {
                //Build ship
                System.out.println("Building ship");
                List<BlockPos> blocks = new ArrayList<>();
                for (int x = Math.min(pos1.getX(), pos2.getX()); x <= Math.max(pos1.getX(), pos2.getX()); x++) {
                    for (int y = Math.min(pos1.getY(), pos2.getY()); y <= Math.max(pos1.getY(), pos2.getY()); y++) {
                        for (int z = Math.min(pos1.getZ(), pos2.getZ()); z <= Math.max(pos1.getZ(), pos2.getZ()); z++) {
                            BlockState state = serverWorld.getBlockState(new BlockPos(new Vec3i(x, y, z)));
                            if (!ShipAssembler.INSTANCE.isValidShipBlock(state)) {
                                continue;
                            }
                            blocks.add(new BlockPos(new Vec3i(x, y, z)));
                        }
                    }
                }
                ship = ShipAssembler.INSTANCE.assembleToShip(serverWorld, blocks, true, 1.0, true);
            }
            NbtHelper.setPositions(context.getStack(), Endurance.INV_POS, Endurance.INV_POS);
        }
        if (pos1.equals(Endurance.INV_POS)) {
            NbtHelper.setPositions(context.getStack(), pos, Endurance.INV_POS);
        } else if (pos2.equals(Endurance.INV_POS)) {
            NbtHelper.setPositions(context.getStack(), pos1, pos);
        } else {
            System.out.println("Resetting");
            NbtHelper.setPositions(context.getStack(), Endurance.INV_POS, Endurance.INV_POS);
        }
        return ActionResult.PASS;
    }
}
