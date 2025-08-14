package com.gorp.auxil.content.computing.reader;

import com.gorp.auxil.AllTileEntities;
import com.gorp.auxil.content.computing.PunchCardItem;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class ReaderBlock extends HorizontalKineticBlock implements ITE<ReaderTileEntity> {

    public static BooleanProperty POWERING = BooleanProperty.create("powering");

    private static VoxelShape SHAPE = VoxelShapes.or(
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(1, 6, 1, 15, 10, 15),
            Block.box(0, 10, 0, 16, 16, 16));

    public ReaderBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERING, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(HORIZONTAL_FACING, state.getValue(HORIZONTAL_FACING).getOpposite());
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
            IItemHandler items = world.getBlockEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);
            if(items != null && !items.getStackInSlot(0).isEmpty()) {
                if(player.addItem(items.getStackInSlot(0))) {
                    return ActionResultType.SUCCESS;
                } else {
                    BlockPos possToAdd = pos.relative(state.getValue(ReaderBlock.HORIZONTAL_FACING));
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
    public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side)
    {
        return true;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        TileEntity tile = blockAccess.getBlockEntity(pos);
        if(tile instanceof ReaderTileEntity && blockState.getValue(HORIZONTAL_FACING).getAxis().test(side)) {
            return ((ReaderTileEntity)tile).getPower();
        }
        return 0;
    }

    @Override
    public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return getSignal(blockState, blockAccess, pos, side);
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

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState stateOther, boolean moving) {
        TileEntity entity = world.getBlockEntity(pos);
        if(entity != null) {
            LazyOptional<IItemHandler> optional = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN);
            if(optional.isPresent()) {
                IItemHandler items = optional.orElseThrow(NullPointerException::new);
                ItemEntity item = new ItemEntity(world, pos.getX() + .5F, pos.getY() + .5F, pos.getZ() + .5F);
                item.spawnAtLocation(items.extractItem(0, 1, false));
            }
        }
        super.onRemove(state, world, pos, stateOther, moving);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return state.getValue(HORIZONTAL_FACING).getAxis().test(side);
    }

    private void updatePower(World pWorld, BlockPos pPos, BlockState pState) {
        BlockPos powerPosition = pPos.relative(pState.getValue(HORIZONTAL_FACING));
        BlockPos otherPowerPosition = pPos.relative(pState.getValue(HORIZONTAL_FACING).getOpposite());
        final boolean isAnalog = pWorld.getBlockState(powerPosition).hasAnalogOutputSignal() || pWorld.getBlockState(otherPowerPosition).hasAnalogOutputSignal();
        withTileEntityDo(pWorld, pPos, (tileEntity) ->
                tileEntity.setAnalog(isAnalog));
    }

    @Override
    public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
        return AllTileEntities.READER.create();
    }

    @Override
    public boolean hasShaftTowards(IWorldReader world, BlockPos pos, BlockState state, Direction face) {
        return getRotationAxis(state).test(face);
    }

    @Override
    public Class<ReaderTileEntity> getTileEntityClass() {
        return ReaderTileEntity.class;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(HORIZONTAL_FACING).getClockWise().getAxis();
    }
}
