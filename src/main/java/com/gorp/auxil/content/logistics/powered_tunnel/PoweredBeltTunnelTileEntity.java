package com.gorp.auxil.content.logistics.powered_tunnel;

import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelTileEntity;
import com.simibubi.create.content.logistics.block.belts.tunnel.BrassTunnelTileEntity;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.INamedIconOptions;
import com.simibubi.create.foundation.tileEntity.behaviour.scrollvalue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.tileentity.TileEntityType;

public class PoweredBeltTunnelTileEntity extends BrassTunnelTileEntity {
    public ScrollOptionBehaviour<RedstoneMode> selectionMode;
    
    public PoweredBeltTunnelTileEntity(TileEntityType<? extends BeltTunnelTileEntity> type) {
        super(type);
    }
    
    public boolean canInput() {
        if(level != null) {
            int power = level.getBestNeighborSignal(getBlockPos());
            switch (selectionMode.get()) {
                case IN_OUT_HIGH:
                case INPUT_HIGH:
                    return power > 0;
                case INPUT_LOW:
                case IN_OUT_LOW:
                    return power < 1;
            }
        }
        return false;
    }
    
    public enum RedstoneMode implements INamedIconOptions {
    
        IN_OUT_HIGH(AllIcons.I_TUNNEL_SPLIT),
        IN_OUT_LOW(AllIcons.I_TUNNEL_SPLIT),
        INPUT_HIGH(AllIcons.I_TUNNEL_SPLIT),
        INPUT_LOW(AllIcons.I_TUNNEL_SPLIT),
        OUTPUT_HIGH(AllIcons.I_TUNNEL_SPLIT),
        OUTPUT_LOW(AllIcons.I_TUNNEL_SPLIT),
        FLOW(AllIcons.I_TUNNEL_SPLIT);
        
        private final String translationKey;
        private final AllIcons icon;
    
        private RedstoneMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "powered_tunnel.redstone_mode." + Lang.asId(this.name());
        }
    
        public AllIcons getIcon() {
            return this.icon;
        }
    
        public String getTranslationKey() {
            return this.translationKey;
        }
    }
    
}
