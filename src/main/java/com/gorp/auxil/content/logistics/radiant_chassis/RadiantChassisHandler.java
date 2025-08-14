package com.gorp.auxil.content.logistics.radiant_chassis;

import com.gorp.auxil.AllBlocks;
import com.gorp.auxil.Auxiliaries;
import com.gorp.auxil.foundation.config.AllConfigs;
import com.gorp.auxil.foundation.networking.Channel;
import com.gorp.auxil.foundation.networking.RadiantChassisPacket;
import com.simibubi.create.CreateClient;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
public class RadiantChassisHandler {

    static BlockPos currentSelection1, currentSelection2;
    static boolean onSecond = false;
    static ItemStack currentItem;

    @SubscribeEvent
    public static void rightClickingBlocksSelectsThem(PlayerInteractEvent.RightClickBlock event) {
        if (currentItem == null)
            return;
        BlockPos pos = event.getPos();
        World world = event.getWorld();
        if (!world.isClientSide)
            return;
        PlayerEntity player = event.getPlayer();
        if (player == null || player.isSpectator() || !player.isCrouching())
            return;

        String key = ".radiant_chassis.target_set";
        TextFormatting color = TextFormatting.WHITE;
        if(!onSecond) {
            key += "1";
            currentSelection1 = pos;
            currentSelection2 = pos;
            onSecond = true;
        } else {
            key += "2";
            currentSelection2 = pos;
        }

        player.sendMessage(new TranslationTextComponent(Auxiliaries.MODID + key).withStyle(color), player.getUUID());
        event.setCanceled(true);
        event.setCancellationResult(ActionResultType.SUCCESS);
    }

    public static void flushSettings(BlockPos pos) {
        if (currentItem == null || currentSelection1 == null)
            return;
        BlockPos pos2 = currentSelection1.subtract(currentSelection2);
        boolean far = Math.abs((pos2.getX() + 1) * (pos2.getY() + 1) * (pos2.getZ() + 1)) > AllConfigs.SERVER.chromaticChassisArea.get();
        if(far) {
            return;
        }
        Channel.channel.sendToServer(new RadiantChassisPacket(pos, currentSelection1, currentSelection2));
        currentItem = null;
        currentSelection1 = null;
        currentSelection2 = null;
        onSecond = false;
    }

    @SubscribeEvent
    public static void leftClickingBlocksDeselectsThem(PlayerInteractEvent.LeftClickBlock event) {
        if (currentItem == null)
            return;
        if (!event.getWorld().isClientSide)
            return;
        if (!event.getPlayer()
                .isCrouching())
            return;

        PlayerEntity player = event.getPlayer();
        BlockPos pos = event.getPos();
        TextFormatting color = TextFormatting.WHITE;
        String key = ".radiant_chassis.target_unset";

        if(!onSecond) {
            currentSelection1 = null;
            key += "1";
        } else {
            currentSelection2 = currentSelection1;
            key += "2";
            onSecond = false;
        }

        player.sendMessage(new TranslationTextComponent(Auxiliaries.MODID + key).withStyle(color), player.getUUID());
        event.setCanceled(true);
        event.setCancellationResult(ActionResultType.FAIL);
    }

    public static void tick() {
        PlayerEntity player = Minecraft.getInstance().player;

        if (player == null)
            return;

        ItemStack heldItemMainhand = player.getItemInHand(Hand.MAIN_HAND);
        if (!AllBlocks.RADIANT_CHASSIS.isIn(heldItemMainhand)) {
            currentItem = null;
        } else {
            if (heldItemMainhand != currentItem) {
                currentSelection1 = null;
                currentSelection2 = null;
                onSecond = false;
                currentItem = heldItemMainhand;
            }

            if(currentSelection1 != null)
                outline();
        }
    }

    private static void outline() {
        RayTraceResult objectMouseOver = Minecraft.getInstance().hitResult;
        if (objectMouseOver instanceof BlockRayTraceResult) {
            BlockRayTraceResult result = (BlockRayTraceResult)objectMouseOver;
            BlockPos pos = result.getBlockPos();
            World world = Minecraft.getInstance().level;
            if(!pos.equals(currentSelection2) && !world.getBlockState(pos).isAir()) {
                drawOutline(pos, pos, true, "2");
            }
        }
        drawOutline(currentSelection1, currentSelection2, false, "");
    }

    private static final int colorUnder = 0xe9ebd8;
    private static final int colorOver = 0xc54b4b;
    private static final int colorHover = 0xf0f0f0;
    
    public static void drawOutline(BlockPos pos1, BlockPos pos2, boolean hover, String identifier) {
        World world = Minecraft.getInstance().level;
        if (pos1 == null || pos2 == null)
            return;
        VoxelShape shape1 = world.getBlockState(pos1).getShape(world, pos1);
        VoxelShape shape2 = world.getBlockState(pos2).getShape(world, pos2);
        
        if(shape1.isEmpty())
            shape1 = Block.box(0, 0, 0, 16, 16, 16);
        
        if(shape2.isEmpty())
            shape2 = Block.box(0, 0, 0, 16, 16, 16);
        
        AxisAlignedBB boundingBox = shape1.bounds().minmax(shape2.bounds().move(pos1.subtract(pos2)));
        double ratio = Math.min(boundingBox.getXsize() * boundingBox.getYsize() * boundingBox.getZsize() / AllConfigs.SERVER.chromaticChassisArea.get(), 1);
        BlockPos pos = currentSelection1.subtract(currentSelection2);
        boolean far = Math.abs((pos.getX() + 1) * (pos.getY() + 1) * (pos.getZ() + 1)) > AllConfigs.SERVER.chromaticChassisArea.get();
        CreateClient.OUTLINER.chaseAABB("target" + identifier, boundingBox.move(pos2))
                .colored(ratio >= 1 && far ? colorOver : hover ? colorHover : colorUnder)
                .lineWidth(1 / 16f);
        
    }
}
