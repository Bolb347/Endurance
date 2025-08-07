package blob.endurance.Block;

import blob.endurance.entities.ModEntities;
import blob.endurance.entities.SeatEntity;
import blob.endurance.vs.SeatAttachment;
import blob.endurance.vs.ThrusterAttachment;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class SeatBlock extends HorizontalFacingBlock {
    private Direction m_dir;
    private LoadedServerShip m_owner;
    private SeatAttachment m_attachment;
    public SeatEntity m_entity;
    public static final MapCodec<SeatBlock> CODEC = createCodec(SeatBlock::new);

    public SeatBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    protected MapCodec<SeatBlock> getCodec() {
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
            m_entity = new SeatEntity(ModEntities.SEAT_ENTITY, world);
            m_entity.setPosition(pos.toCenterPos());
            m_entity.setYaw(m_dir.asRotation());
            m_entity.bind(m_owner, pos, m_dir);
            world.spawnEntity(m_entity);
            m_attachment = m_owner.getAttachment(SeatAttachment.class);
            if (m_attachment == null) {
                m_owner.saveAttachment(ThrusterAttachment.class, new ThrusterAttachment());
                m_attachment = m_owner.getAttachment(SeatAttachment.class);
            }
            if (m_attachment == null) {
                return;
            }
            m_attachment.attachSeat(pos, m_dir);
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (m_attachment != null) {
            m_attachment.removeSeat(pos);
            m_entity.discard();
        }
    }
}