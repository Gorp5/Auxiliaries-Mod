package com.gorp.auxil.content.logistics.turbine;

import com.gorp.auxil.AllTileEntities;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.content.contraptions.fluids.FluidPropagator;
import com.simibubi.create.content.contraptions.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.contraptions.fluids.PumpTileEntity;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class TurbineBlock extends DirectionalKineticBlock implements ICogWheel, IWaterLoggable {
    
    public TurbineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)super.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{BlockStateProperties.WATERLOGGED});
        super.createBlockStateDefinition(builder);
    }
    
    @Override
    public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
        return AllTileEntities.TURBINE.create();
    }
    
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState FluidState = context.getLevel().getFluidState(context.getClickedPos());
        return (BlockState)super.getStateForPlacement(context).setValue(BlockStateProperties.WATERLOGGED, FluidState.getType() == Fluids.WATER);
    }
    
    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.getBlockTicks().scheduleTick(pos, this, 10, TickPriority.HIGH);
        FluidPropagator.propagateChangedPipe(worldIn, pos, state);
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }
    
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block otherBlock, BlockPos neighborPos, boolean isMoving) {
        Direction[] var6 = Iterate.directions;
        int var7 = var6.length;
        Direction p = null;
        for(int var8 = 0; var8 < var7; ++var8) {
            Direction d = var6[var8];
            if (pos.relative(d).equals(neighborPos)) {
                p = d;
            }
        }
        if (p != null) {
            FluidPropagator.propagateChangedPipe(world, pos, state);
            world.getBlockTicks().scheduleTick(pos, this, 10, TickPriority.HIGH);
        }
    }
    
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
        if(state.getBlock() instanceof TurbineBlock) {
            FluidTransportBehaviour thisPipe = TileEntityBehaviour.get(world, pos, FluidTransportBehaviour.TYPE);
            Pair<List<FluidTransportBehaviour>, List<TurbineTileEntity>> pumpsAndTurbines = findPumpsAndTurbines(thisPipe, world);
            List<FluidTransportBehaviour> pumps = pumpsAndTurbines.getFirst();
            List<TurbineTileEntity> turbines = pumpsAndTurbines.getSecond();
            for(FluidTransportBehaviour pump : pumps)
                for (TurbineTileEntity turbine : turbines)
                    turbine.addGenerationSpeed(Math.abs(((PumpTileEntity)pump.tileEntity).getSpeed() / turbines.size()));
        }
    }
    
    private Pair<List<FluidTransportBehaviour>, List<TurbineTileEntity>> findPumpsAndTurbines(FluidTransportBehaviour root, ServerWorld world) {
        LinkedList<FluidTransportBehaviour> visited = new LinkedList<>();
        Queue<FluidTransportBehaviour> queue = new LinkedBlockingQueue<>();
        
        queue.add(root);
        List<FluidTransportBehaviour> pumps = new LinkedList<>();
        List<TurbineTileEntity> turbines = new LinkedList<>();
        
        do {
            FluidTransportBehaviour pipe = queue.poll();
            if(visited.contains(pipe))
                continue;
            visited.add(pipe);
            if(pipe.tileEntity instanceof PumpTileEntity)
                pumps.add(pipe);
            if(pipe.tileEntity instanceof TurbineTileEntity) {
                TurbineTileEntity turbineTile = ((TurbineTileEntity)pipe.tileEntity);
                turbineTile.resetSpeed();
                turbines.add(turbineTile);
            }
            List<Direction> list = FluidPropagator.getPipeConnections(pipe.tileEntity.getBlockState(), pipe);
            for(Direction dir : list) {
                FluidTransportBehaviour toqueue = FluidPropagator.getPipe(world, pipe.getPos().relative(dir));
                if(toqueue != null)
                    queue.add(toqueue);
            }
        } while (!queue.isEmpty());
        
        return Pair.of(pumps, turbines);
    }
    
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        return (BlockState)originalState.setValue(FACING, ((Direction)originalState.getValue(FACING)).getOpposite());
    }
    
    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(FACING).getAxis();
    }
}
