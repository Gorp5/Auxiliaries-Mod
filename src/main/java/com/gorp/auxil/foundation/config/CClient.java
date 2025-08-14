package com.gorp.auxil.foundation.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class CClient extends ConfigBase {
    //public ConfigInt ingameMenuConfigButtonOffsetX = this.i(-4, -2147483648, 2147483647, "ingameMenuConfigButtonOffsetX", new String[]{"Offset the Create config button in the in-game menu by this many pixels on the X axis", "The sign (+/-) of this value determines what side of the row the button appears on (right/left)"});
    //public ConfigBool ignoreFabulousWarning = this.b(false, "ignoreFabulousWarning", new String[]{"Setting this to true will prevent Create from sending you a warning when playing with Fabulous graphics enabled"});
    //public ConfigGroup placementAssist = this.group(1, "placementAssist", new String[]{"Settings for the Placement Assist"});
    
    public CClient() {}
    
    public String getName() {
        return "client";
    }
}
