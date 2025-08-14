package com.gorp.auxil.foundation.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CServer extends ConfigBase {
    public ConfigFloat blastEnchantPowerMultiplier;
    public ConfigInt chromaticChassisArea;
    
    public CServer() {
        blastEnchantPowerMultiplier = this.f(1, 0, 20, "blastEnchantPowerMultiplier", Comments.blastEnchantPowerMultiplier);
        chromaticChassisArea = this.i(27, 1, 64, "chromaticChassisArea", Comments.chromaticChassisArea);
    }
    
    public String getName() {
        return "server";
    }
    
    private static class Comments {
        static String blastEnchantPowerMultiplier = "The power multiplier of the blast enchantment on potato cannons";
        static String chromaticChassisArea = "The amount of blocks a chromatic chassis can effect at one time";
        
        private Comments() {
        }
    }
}
