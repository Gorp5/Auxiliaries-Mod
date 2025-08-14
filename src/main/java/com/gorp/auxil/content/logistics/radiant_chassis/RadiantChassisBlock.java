package com.gorp.auxil.content.logistics.radiant_chassis;

import com.gorp.auxil.AllTileEntities;
import com.gorp.auxil.content.logistics.Tube;
import com.gorp.auxil.content.logistics.shadow_chassis.ShadowChassisTileEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.AbstractChassisBlock;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class RadiantChassisBlock extends AbstractChassisBlock implements ITE<RadiantChassisTileEntity> {
    
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public RadiantChassisBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }
    
    @Override
    public void neighborChanged(BlockState pState, World pWorldIn, BlockPos pPos, Block pBlockIn, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pWorldIn, pPos, pBlockIn, pFromPos, pIsMoving);
        if(pWorldIn.getBestNeighborSignal(pPos) > 0)
            pWorldIn.setBlock(pPos, pState.setValue(POWERED, true), 3);
    }
    
    public BooleanProperty getGlueableSide(BlockState state, Direction face) {
        return null;
    }
    
    protected boolean glueAllowedOnSide(IBlockReader world, BlockPos pos, BlockState state, Direction side) {
        return false;
    }
    
    public static boolean isChassis(BlockState state) {
        return AllBlocks.RADIAL_CHASSIS.has(state);
    }
    
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return AllTileEntities.RADIANT_CHASSIS.create();
    }
    
    @Override
    public Class<RadiantChassisTileEntity> getTileEntityClass() {
        return RadiantChassisTileEntity.class;
    }
}

