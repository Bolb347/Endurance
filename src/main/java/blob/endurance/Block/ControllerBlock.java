package blob.endurance.Block;

import blob.endurance.Endurance;
import blob.endurance.entities.ModEntities;
import blob.endurance.entities.ControllerEntity;
import blob.endurance.vs.ControllerAttachment;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ControllerBlock extends HorizontalFacingBlock {
    private Direction m_dir;
    private LoadedServerShip m_owner;
    private ControllerAttachment m_attachment;
    public ControllerEntity m_entity;
    public static final MapCodec<ControllerBlock> CODEC = createCodec(ControllerBlock::new);

    public ControllerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    protected MapCodec<ControllerBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
//        m_dir = state.get(Properties.HORIZONTAL_FACING);;
//        if (!world.isClient) {
//            LoadedServerShip m_owner = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
//            if (m_owner != null) {
//                m_entity = new ControllerEntity(ModEntities.CONTROLLER_ENTITY, world);
//                m_entity.setPosition(pos.toCenterPos());
//                m_entity.setYaw(m_dir.asRotation());
//                m_entity.bind(m_owner);
//                if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
//                    world.spawnEntity(m_entity);
//                    Endurance.CONTROLLERS.put(pos, m_entity);
//                } else {
//                    m_entity.discard();
//                }
//            }
//            m_attachment = m_owner.getAttachment(ControllerAttachment.class);
//            if (m_attachment == null) {
//                m_owner.saveAttachment(ControllerAttachment.class, new ControllerAttachment());
//                m_attachment = m_owner.getAttachment(ControllerAttachment.class);
//            }
//            if (m_attachment == null) {
//                return;
//            }
//            m_attachment.attachController(pos, m_dir);
//        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (m_attachment != null) {
            Endurance.CONTROLLERS.remove(pos);
            m_attachment.removeController(pos);
            m_entity.discard();
        }
    }
}