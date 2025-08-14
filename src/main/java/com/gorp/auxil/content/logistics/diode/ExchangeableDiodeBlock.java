package com.gorp.auxil.content.logistics.diode;

import com.gorp.auxil.AllItems;
import com.gorp.auxil.AllTileEntities;
import com.gorp.auxil.content.logistics.Tube;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class ExchangeableDiodeBlock extends DirectionalBlock implements IWrenchable, ITE<ExchangeableDiodeTileEntity> {

    private static VoxelShape SHAPE_BASE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 3.0D, 12.0D);
    private static VoxelShape SHAPE_TUBE = Block.box(5.0D, 3.0D, 5.0D, 11.0D, 13.0D, 11.0D);
    public static Property<Tube> TUBE = EnumProperty.create("tube_type", Tube.class);
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    
    public ExchangeableDiodeBlock(Properties builder) {
        super(builder);
        this.registerDefaultState(defaultBlockState().setValue(TUBE, Tube.EMPTY).setValue(POWER, 0).setValue(FACING, Direction.UP));
    }
    
    public boolean canSurvive(BlockState pState, IWorldReader pWorldIn, BlockPos pPos) {
        return canSupportRigidBlock(pWorldIn, pPos.below());
    }
    
    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        if(state.getValue(TUBE) == Tube.RADIANT)
            return 15;
        else if(state.getValue(TUBE) == Tube.DISCHARGE)
            return state.getValue(POWER);
        return super.getLightValue(state, world, pos);
    }
    
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return AllTileEntities.DIODE_SOCKET.create();
    }
    
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(TUBE);
        builder.add(POWER);
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }

    public VoxelShape getShape(BlockState pState, IBlockReader pWorldIn, BlockPos pPos, ISelectionContext pContext) {
        if(pState.getValue(TUBE) == Tube.EMPTY)
            return SHAPE_BASE;
        return VoxelShapes.or(SHAPE_BASE, SHAPE_TUBE);
    }

    @Override
    public ActionResultType onWrenched(BlockState state, ItemUseContext context) {
        Tube type = state.getValue(TUBE);
        context.getLevel().setBlock(context.getClickedPos(), state.setValue(TUBE, Tube.EMPTY), 3);
        context.getLevel().updateNeighbourForOutputSignal(context.getClickedPos(), state.getBlock());
        TileEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());
        if(tile instanceof ExchangeableDiodeTileEntity)
            ((ExchangeableDiodeTileEntity)tile).setTubeState(Tube.EMPTY);
        if(!context.getPlayer().isCreative()) {
            BlockPos pos = context.getClickedPos();
            ItemEntity item = new ItemEntity(context.getLevel(), pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
            ItemStack stack = getItem(type);
            if (stack != null)
                item.spawnAtLocation(stack);
        }
        return ActionResultType.SUCCESS;
    }
    
    @Override
    public void onPlace(BlockState pState, World pWorldIn, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        int power = pWorldIn.getBestNeighborSignal(pPos);
        pWorldIn.setBlock(pPos, pState.setValue(POWER, power), 3);
        super.onPlace(pState, pWorldIn, pPos, pOldState, pIsMoving);
    }
    
    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        Tube type = state.getValue(TUBE);
        ItemEntity item = new ItemEntity(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
        ItemStack stack = getItem(type);
        if(stack != null && !player.isCreative())
            item.spawnAtLocation(stack);
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return true;
    }
    
    @Override
    public int getAnalogOutputSignal(BlockState pBlockState, World pWorldIn, BlockPos pPos) {
        ExchangeableDiodeTileEntity diode = getTileEntity(pWorldIn, pPos);
        if(diode != null)
            return diode.getComparatorOutput();
        return super.getAnalogOutputSignal(pBlockState, pWorldIn, pPos);
    }
    
    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        switch (pState.getValue(TUBE)) {
            case ELECTRON:
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public void neighborChanged(BlockState pState, World pWorldIn, BlockPos pPos, Block pBlockIn, BlockPos pFromPos, boolean pIsMoving) {
        if (!pState.canSurvive(pWorldIn, pPos)) {
            TileEntity tileentity = pState.hasTileEntity() ? pWorldIn.getBlockEntity(pPos) : null;
            dropResources(pState, pWorldIn, pPos, tileentity);
            pWorldIn.removeBlock(pPos, false);
            for(Direction direction : Direction.values()) {
                pWorldIn.updateNeighborsAt(pPos.relative(direction), this);
            }
        }
        super.neighborChanged(pState, pWorldIn, pPos, pBlockIn, pFromPos, pIsMoving);
        int power = pWorldIn.getBestNeighborSignal(pPos);
        pWorldIn.setBlock(pPos, pState.setValue(POWER, power), 3);
    }
    
    @Override
    public int getSignal(BlockState pBlockState, IBlockReader pBlockAccess, BlockPos pPos, Direction pSide) {
        return getOutputSignal(pBlockAccess, pPos, pBlockState);
    }
    
    protected int getOutputSignal(IBlockReader pWorldIn, BlockPos pPos, BlockState pState) {
        TileEntity tile = pWorldIn.getBlockEntity(pPos);
        ExchangeableDiodeTileEntity diode = getTileEntity(pWorldIn, pPos);
        if(diode != null)
            return (int)diode.getOutputPower();
        return 0;
    }

    private ItemStack getItem(Tube type) {
        switch (type) {
            case ELECTRON:
                return com.simibubi.create.AllItems.ELECTRON_TUBE.asStack();
            case PHOTO:
                return AllItems.PHOTOTUBE.asStack();
            case DISCHARGE:
                return AllItems.DISCHARGE_TUBE.asStack();
            case RADIANT:
                return AllItems.RADIANT_TUBE.asStack();
            default:
                return null;
        }
    }

    private Tube getType(ItemStack stack) {
        if(stack.getItem() == AllItems.PHOTOTUBE.get())
            return Tube.PHOTO;
        if(stack.getItem() == com.simibubi.create.AllItems.ELECTRON_TUBE.get())
            return Tube.ELECTRON;
        if(stack.getItem() == AllItems.RADIANT_TUBE.get())
            return Tube.RADIANT;
        if(stack.getItem() == AllItems.DISCHARGE_TUBE.get())
            return Tube.DISCHARGE;
        return Tube.EMPTY;
    }
    
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        ItemStack stack = player.getItemInHand(hand);
        Tube type = getType(stack);
        if(type != Tube.EMPTY && state.getValue(TUBE) == Tube.EMPTY) {
            world.setBlock(pos, state.setValue(TUBE, type).setValue(POWER, 0), 3);
            TileEntity tile = world.getBlockEntity(pos);
            if(tile instanceof ExchangeableDiodeTileEntity)
                ((ExchangeableDiodeTileEntity)tile).setTubeState(type);
            if(!player.isCreative())
                stack.shrink(1);
            player.setItemInHand(hand, stack);
            return ActionResultType.CONSUME;
        }
        return super.use(state, world, pos, player, hand, rayTraceResult);
    }
    
    @Override
    public Class<ExchangeableDiodeTileEntity> getTileEntityClass() {
        return ExchangeableDiodeTileEntity.class;
    }
}
