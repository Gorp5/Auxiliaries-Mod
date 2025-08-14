package com.gorp.auxil.content.computing.etcher;

import com.gorp.auxil.AllTileEntities;
import com.gorp.auxil.content.computing.PunchCardItem;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.DirectionHelper;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class EtcherBlock extends HorizontalKineticBlock implements ICogWheel, ITE<EtcherTileEntity>, IWrenchable {

    public static BooleanProperty POWERED = BooleanProperty.create("powered");
    public static BooleanProperty OVERFLOW = BooleanProperty.create("overflow");

    protected static final VoxelShape SHAPE;

    static {
        VoxelShape bottom = Block.box(0, 0, 0, 16, 6, 16);
        VoxelShape middle = Block.box(0, 10, 0, 16, 4, 16);
        VoxelShape mesh = Block.box(1, 14, 1, 14, 2, 14);
        VoxelShape top = Block.box(0, 0, 0, 16, 14, 16);
        SHAPE = VoxelShapes.or(bottom, middle, mesh);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public EtcherBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(OVERFLOW, false));
    }

    public void neighborChanged(BlockState pState, World pWorldIn, BlockPos pPos, Block pBlockIn, BlockPos pFromPos, boolean pIsMoving) {
        if (!pWorldIn.isClientSide) {
            if (pState.canSurvive(pWorldIn, pPos)) {
                this.updatePower(pWorldIn, pPos, pState);
            } else {
                pWorldIn.removeBlock(pPos, false);
            }
        }
    }

    private void updatePower(World pWorld, BlockPos pPos, BlockState pState) {
        int power = 0;
        BlockPos powerPosition = pPos;
        for(Direction direction : Direction.values()) {
            BlockPos pos = pPos.relative(direction);
            int j = pWorld.getSignal(pos, direction);
            if (j >= 15) {
                power = 15;
                powerPosition = pos;
                break;
            }

            if (j > power) {
                power = j;
                powerPosition = pos;
            }
        }

        final int finalPower = power;
        final boolean isAnalog = pWorld.getBlockState(powerPosition).hasAnalogOutputSignal();
        withTileEntityDo(pWorld, pPos.above(), (tileEntity) ->
        {
            tileEntity.notifySignal(finalPower);
            tileEntity.setAnalog(isAnalog);
        });

        if(power > 0 != pState.getValue(POWERED))
            pWorld.setBlock(pPos, pState.setValue(POWERED, power > 0), 51);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        ItemStack inHand = player.getItemInHand(hand);
        if(inHand.getItem() instanceof PunchCardItem) {
            IItemHandler items = world.getBlockEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
            if(items != null) {
                ItemStack stack = items.insertItem(0, inHand, false);
                player.setItemInHand(hand, stack);
                return ActionResultType.SUCCESS;
            }
        } else {
            IItemHandler items = world.getBlockEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, state.getValue(EtcherBlock.HORIZONTAL_FACING)).orElse(null);
            if(items != null && !items.getStackInSlot(0).isEmpty()) {
                if(player.addItem(items.getStackInSlot(0))) {
                    return ActionResultType.SUCCESS;
                } else {
                    BlockPos possToAdd = pos.relative(state.getValue(EtcherBlock.HORIZONTAL_FACING));
                    ItemEntity entity = new ItemEntity(world, possToAdd.getX(), possToAdd.getY(), possToAdd.getZ(), items.getStackInSlot(0));
                    world.addFreshEntity(entity);
                    world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundCategory.BLOCKS, .6F, 1F, false);
                }
                items.extractItem(0, 1, false);
            }
        }
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState stateOther, boolean moving) {
        TileEntity entity = world.getBlockEntity(pos);
        if (entity != null) {
            LazyOptional<IItemHandler> optional = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, state.getValue(HORIZONTAL_FACING));
            if (optional.isPresent()) {
                IItemHandler items = optional.orElseThrow(NullPointerException::new);
                ItemEntity item = new ItemEntity(world, pos.getX() + .5F, pos.getY() + .5F, pos.getZ() + .5F);
                item.spawnAtLocation(items.extractItem(0, 1, false));
            }
        }
        super.onRemove(state, world, pos, stateOther, moving);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        builder.add(OVERFLOW);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }

    public ActionResultType onWrenched(BlockState state, ItemUseContext context) {
        World world = context.getLevel();
        BlockState rotated = this.getRotatedBlockState(state, context.getClickedFace());
        if (!rotated.canSurvive(world, context.getClickedPos())) {
            return ActionResultType.PASS;
        } else {
            KineticTileEntity.switchToBlockState(world, context.getClickedPos(), this.updateAfterWrenched(rotated, context));
            TileEntity te = context.getLevel().getBlockEntity(context.getClickedPos());

            if (te != null)
                te.clearCache();

            if (world.getBlockState(context.getClickedPos()) != state) {
                this.playRotateSound(world, context.getClickedPos());
            }

            return ActionResultType.SUCCESS;
        }
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        if (originalState.hasProperty(HorizontalKineticBlock.HORIZONTAL_FACING)) {
            return originalState.setValue(HorizontalKineticBlock.HORIZONTAL_FACING, DirectionHelper.rotateAround(originalState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING), Direction.UP.getAxis()));
        }
        return super.getRotatedBlockState(originalState, targetedFace);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return (EtcherTileEntity)AllTileEntities.ETCHER.create();
    }

    @Override
    public boolean hasShaftTowards(IWorldReader iWorldReader, BlockPos blockPos, BlockState blockState, Direction direction) {
        return false;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return Direction.DOWN.getAxis();
    }

    @Override
    public Class<EtcherTileEntity> getTileEntityClass() {
        return EtcherTileEntity.class;
    }
}
