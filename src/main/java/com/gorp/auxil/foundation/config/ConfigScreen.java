package com.gorp.auxil.foundation.config;

import com.gorp.auxil.Auxiliaries;
import com.simibubi.create.foundation.config.ui.BaseConfigScreen;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nonnull;

public class ConfigScreen extends BaseConfigScreen {
    public static ConfigScreen forAux(Screen parent) {
        return (ConfigScreen) new ConfigScreen(parent, Auxiliaries.MODID)
                .withTitles("Client Settings", "World Generation Settings", "Gameplay Settings")
                .withSpecs(AllConfigs.CLIENT.specification, AllConfigs.COMMON.specification, AllConfigs.SERVER.specification);
    }
    
    public ConfigScreen(Screen parent, @Nonnull String modID) {
        super(parent, modID);
    }
}