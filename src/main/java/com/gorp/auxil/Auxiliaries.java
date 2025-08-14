package com.gorp.auxil;

import com.gorp.auxil.foundation.config.AllConfigs;
import com.gorp.auxil.foundation.networking.Channel;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.NonNullLazyValue;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("auxil")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Auxiliaries
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static String MODID = "auxil";

    public static ItemGroup creativeTab = new ItemGroup("Auxiliaries Tab") {
        @Override
        public ItemStack makeIcon() {
            return AllItems.PUNCHER.asStack();
        }
    };

    private static final NonNullLazyValue<CreateRegistrate> REGISTRATE = CreateRegistrate.lazy(Auxiliaries.MODID);
    
    public Auxiliaries() {
        AllBlocks.register();
        AllItems.register();
        AllConfigs.register();
        AllTileEntities.register();
        AllEntityTypes.register();
        AllEnchantments.register();
        
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        IEventBus modEventBus = FMLJavaModLoadingContext.get()
                .getModEventBus();
        
        forgeEventBus.register(this);
        forgeEventBus.register(ClientEvents.class);
        forgeEventBus.register(ServerEvents.class);
        modEventBus.register(Auxiliaries.class);
        modEventBus.addListener(AllConfigs::onLoad);
        modEventBus.addListener(AllConfigs::onReload);
        modEventBus.addListener(Auxiliaries::init);
    }
    
    @SubscribeEvent
    public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {
        if (event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            event.addSprite(TextureSpriteShifter.POWERED_TUNNEL.getTargetResourceLocation());
        }
    }
    
    public static void init(final FMLCommonSetupEvent event) {
        Channel.registerPackets();
    }
    
    public static CreateRegistrate registrate() {
        return REGISTRATE.get().itemGroup(() -> Auxiliaries.creativeTab);
    }
    
    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
