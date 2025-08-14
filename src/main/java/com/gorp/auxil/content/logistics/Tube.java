package com.gorp.auxil.content.logistics;

import net.minecraft.util.IStringSerializable;

public enum Tube implements IStringSerializable {
    ELECTRON("electron"),
    PHOTO("photo"),
    DISCHARGE("discharge"),
    RADIANT("radiant"),
    EMPTY("empty")
    ;

    public String name;

    Tube(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static Tube fromString(String name) {
        for(Tube type : Tube.values())
            if(type.getSerializedName().equals(name))
                return type;
            return Tube.EMPTY;
    }
}
