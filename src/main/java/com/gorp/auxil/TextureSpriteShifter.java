package com.gorp.auxil;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class TextureSpriteShifter {

    protected static Map<String, SpriteShiftEntry> textures = new HashMap();

    public TextureSpriteShifter() {
    }

    public static CTSpriteShiftEntry POWERED_TUNNEL = getCT(CTSpriteShifter.CTType.OMNIDIRECTIONAL, "powered_tunnel");

    public static CTSpriteShiftEntry getCT(CTSpriteShifter.CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

    public static CTSpriteShiftEntry getCT(CTSpriteShifter.CTType type, String blockTextureName, String connectedTextureName) {
        return getCT(type, new ResourceLocation(Auxiliaries.MODID, "block/" + blockTextureName), connectedTextureName);
    }

    public static CTSpriteShiftEntry getCT(CTSpriteShifter.CTType type, ResourceLocation blockTexture, String connectedTextureName) {
        String targetLocation = "block/" + connectedTextureName + "_connected";
        String key = type.name() + ":" + blockTexture.getNamespace() + ":" + blockTexture.getPath() + "->" + targetLocation;
        if (textures.containsKey(key)) {
            return (CTSpriteShiftEntry)textures.get(key);
        } else {
            CTSpriteShiftEntry entry = create(type);
            ResourceLocation targetTextureLocation = new ResourceLocation(Auxiliaries.MODID, targetLocation);
            entry.set(blockTexture, targetTextureLocation);
            textures.put(key, entry);
            return entry;
        }
    }

    private static CTSpriteShiftEntry create(CTSpriteShifter.CTType type) {
        switch(type) {
            case HORIZONTAL:
                return new CTSpriteShiftEntry.Horizontal();
            case OMNIDIRECTIONAL:
                return new CTSpriteShiftEntry.Omnidirectional();
            case VERTICAL:
                return new CTSpriteShiftEntry.Vertical();
            case CROSS:
                return new CTSpriteShiftEntry.Cross();
            default:
                return null;
        }
    }
}
