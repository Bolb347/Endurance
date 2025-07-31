package blob.endurance.Block;

import blob.endurance.vs.ThrusterAttachment;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
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

public class ThrusterBlock extends FacingBlock {
    private Direction m_dir;
    private LoadedServerShip m_owner;
    private ThrusterAttachment m_attachment;
    public static final MapCodec<ThrusterBlock> CODEC = createCodec(ThrusterBlock::new);

    public ThrusterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    protected MapCodec<ThrusterBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        m_dir = state.get(Properties.FACING);
        if (!world.isClient) {
            LoadedServerShip m_owner = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, pos);
            if (m_owner == null) {
                return;
            }
            m_attachment = m_owner.getAttachment(ThrusterAttachment.class);
            if (m_attachment == null) {
                m_owner.saveAttachment(ThrusterAttachment.class, new ThrusterAttachment());
                m_attachment = m_owner.getAttachment(ThrusterAttachment.class);
            }
            if (m_attachment == null) {
                return;
            }
            m_attachment.attachThruster(pos, m_dir);
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (m_attachment != null) {
            m_attachment.removeThruster(pos);
        }
    }
}