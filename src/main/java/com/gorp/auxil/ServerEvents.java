package com.gorp.auxil;

import com.gorp.auxil.foundation.networking.ShadowChassisAreaHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

@Mod.EventBusSubscriber
public class ServerEvents {
    
    @SubscribeEvent
    public static void onServerStart(FMLServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        ShadowChassisAreaHandler.register();
        ShadowChassisAreaHandler.setServer(server);
    }
    
    public static void onDrop(LivingDropsEvent event) {
    }
}