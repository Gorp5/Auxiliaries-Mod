package com.gorp.auxil.foundation.config;
import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

public class AllConfigs {
    static Map<ConfigBase, ModConfig.Type> configs = new HashMap<>();
    public static CClient CLIENT;
    public static CCommon COMMON;
    public static CServer SERVER;
    
    public AllConfigs() {
    }
    
    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ForgeConfigSpec> specPair = (new ForgeConfigSpec.Builder()).configure((builder) -> {
            T config = (T)(ConfigBase)factory.get();
            try {
                Class<ConfigBase> clazz = ConfigBase.class;
                Method method = clazz.getDeclaredMethod("registerAll", ForgeConfigSpec.Builder.class);
                method.setAccessible(true);
                method.invoke(config, builder);
            } catch (Exception e) {}
            return config;
        });
        T config = (T) specPair.getLeft();
        config.specification = (ForgeConfigSpec)specPair.getRight();
        configs.put(config, side);
        return config;
    }
    
    public static void register() {
        CLIENT = (CClient)register(CClient::new, ModConfig.Type.CLIENT);
        COMMON = (CCommon)register(CCommon::new, ModConfig.Type.COMMON);
        SERVER = (CServer)register(CServer::new, ModConfig.Type.SERVER);
        Iterator<Map.Entry<ConfigBase, ModConfig.Type>> var0 = configs.entrySet().iterator();
        
        while(var0.hasNext()) {
            Map.Entry<ConfigBase, ModConfig.Type> pair = var0.next();
            ModLoadingContext.get().registerConfig((ModConfig.Type)pair.getValue(), ((ConfigBase)pair.getKey()).specification);
        }
    }
    
    public static void onLoad(ModConfig.Loading event) {
        Iterator<Map.Entry<ConfigBase, ModConfig.Type>> var1 = configs.entrySet().iterator();
        
        while(var1.hasNext()) {
            Map.Entry<ConfigBase, ModConfig.Type> pair = var1.next();
            if (((ConfigBase)pair.getKey()).specification == event.getConfig().getSpec()) {
                ((ConfigBase)pair.getKey()).onLoad();
            }
        }
        
    }
    
    public static void onReload(ModConfig.Reloading event) {
        Iterator<Map.Entry<ConfigBase, ModConfig.Type>> var1 = configs.entrySet().iterator();
        
        while(var1.hasNext()) {
            Map.Entry<ConfigBase, ModConfig.Type> pair = var1.next();
            if (((ConfigBase)pair.getKey()).specification == event.getConfig().getSpec()) {
                ((ConfigBase)pair.getKey()).onReload();
            }
        }
        
    }
}

