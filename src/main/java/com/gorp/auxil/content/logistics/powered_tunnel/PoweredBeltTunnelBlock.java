package com.gorp.auxil.content.logistics.powered_tunnel;

import com.gorp.auxil.AllTileEntities;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelBlock;
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelItem;
import com.simibubi.create.content.logistics.block.belts.tunnel.BrassTunnelBlock;
import com.simibubi.create.content.logistics.block.belts.tunnel.BrassTunnelTileEntity;
import com.simibubi.create.content.logistics.block.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.block.funnel.BrassFunnelBlock;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringBehaviour;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class PoweredBeltTunnelBlock extends BeltTunnelBlock {
    
    public static BooleanProperty POWERED = BlockStateProperties.POWERED;
    public PoweredBeltTunnelBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }
    
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context).setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }
    
    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        worldIn.setBlock(pos, state.setValue(POWERED, worldIn.getBestNeighborSignal(pos) > 0), 3);
    }
    
    public ActionResultType use(BlockState p_225533_1_, World world, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        return this.onTileEntityUse(world, pos, (te) -> {
            if (!(te instanceof PoweredBeltTunnelTileEntity)) {
                return ActionResultType.PASS;
            } else {
                PoweredBeltTunnelTileEntity bte = (PoweredBeltTunnelTileEntity)te;
                List<ItemStack> stacksOfGroup = bte.grabAllStacksOfGroup(world.isClientSide);
                if (stacksOfGroup.isEmpty()) {
                    return ActionResultType.PASS;
                } else if (world.isClientSide) {
                    return ActionResultType.SUCCESS;
                } else {
                    Iterator var6 = stacksOfGroup.iterator();
                    
                    while(var6.hasNext()) {
                        ItemStack itemStack = (ItemStack)var6.next();
                        player.inventory.placeItemBackInInventory(world, itemStack.copy());
                    }
                    world.playSound((PlayerEntity)null, pos, SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, 1.0F + Create.RANDOM.nextFloat());
                    return ActionResultType.SUCCESS;
                }
            }
        });
    }
    
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return AllTileEntities.POWERED_TUNNEL.create();
    }
    
    public void onRemove(BlockState p_196243_1_, World p_196243_2_, BlockPos p_196243_3_, BlockState p_196243_4_, boolean p_196243_5_) {
        if (p_196243_1_.hasTileEntity() && (p_196243_1_.getBlock() != p_196243_4_.getBlock() || !p_196243_4_.hasTileEntity())) {
            TileEntityBehaviour.destroy(p_196243_2_, p_196243_3_, FilteringBehaviour.TYPE);
            this.withTileEntityDo(p_196243_2_, p_196243_3_, (te) -> {
                if (te instanceof PoweredBeltTunnelTileEntity) {
                    Block.popResource(p_196243_2_, p_196243_3_, ((PoweredBeltTunnelTileEntity)te).getStackToDistribute());
                }
                
            });
            p_196243_2_.removeBlockEntity(p_196243_3_);
        }
        
    }
}
