package com.gorp.auxil.foundation.networking;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ShadowChassisAreaHandler {
    
    public static final ShadowChassisAreaHandler INSTANCE = new ShadowChassisAreaHandler();
    private ShadowChassisAreaHandler() {
        protectedBlocks = new LinkedHashMap<>();
    }
    public static void register() {};
    
    public static MinecraftServer server;
    public static void setServer(MinecraftServer serv) {
        server = serv;
    }
    
    public static LinkedHashMap<BlockPos, BlockPos> protectedBlocks;
    public static void addProtectedBlocks(BlockPos anchor, BlockPos sizeIndicator) {
        if(anchor != null && sizeIndicator != null)
            protectedBlocks.put(anchor, sizeIndicator);
    }
    
    public static boolean removeProtectedBlocks(BlockPos anchor, BlockPos sizeIndicator) {
        if(anchor != null && sizeIndicator != null)
            return protectedBlocks.remove(anchor) != null;
        return false;
    }
    
    public static boolean containsBlock(BlockPos pos) {
        Set<Map.Entry<BlockPos, BlockPos>> set =  protectedBlocks.entrySet();
        for(Map.Entry<BlockPos, BlockPos> pair : set) {
            BlockPos anchor = pair.getKey();
            BlockPos sizeIndicator = pair.getValue();
            if(pos.getX() >= Math.min(anchor.getX(), sizeIndicator.getX()) && pos.getX() <= Math.max(anchor.getX(), sizeIndicator.getX())) {
                if(pos.getY() >= Math.min(anchor.getY(), sizeIndicator.getY()) && pos.getY() <= Math.max(anchor.getY(), sizeIndicator.getY())) {
                    if(pos.getZ() >= Math.min(anchor.getZ(), sizeIndicator.getZ()) && pos.getZ() <= Math.max(anchor.getZ(), sizeIndicator.getZ())) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public static boolean doesNotContainBlock(BlockPos pos) {
        return !containsBlock(pos);
    }
}
