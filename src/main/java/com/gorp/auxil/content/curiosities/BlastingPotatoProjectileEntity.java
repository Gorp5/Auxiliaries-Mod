package com.gorp.auxil.content.curiosities;

import com.gorp.auxil.foundation.config.AllConfigs;
import com.simibubi.create.content.contraptions.particle.AirFlowParticleData;
import com.simibubi.create.content.curiosities.weapons.PotatoCannonProjectileTypes;
import com.simibubi.create.content.curiosities.weapons.PotatoProjectileEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class BlastingPotatoProjectileEntity extends PotatoProjectileEntity {
    private int blastLevel = 0;
    public BlastingPotatoProjectileEntity(EntityType<? extends DamagingProjectileEntity> type, World world) {
        super(type, world);
    }
    
    public void setBlastLevel(int blastLevel) {
        this.blastLevel = blastLevel;
    
    }
    
    @Override
    public void remove() {
        if(blastLevel > 0) {
            AxisAlignedBB alignedBB = new AxisAlignedBB(new BlockPos(getPosition(1))).inflate(blastLevel * 2);
            final Vector3d posVec = getPosition(1).add(0.0F, -1.0F, 0.0F);
            List<Entity> nearbyEntities = level.getEntities(this, alignedBB, Entity::isAlive);
            nearbyEntities.forEach((entity) -> {
                Vector3d distanceVec =  entity.getPosition(1).subtract(posVec);
                Vector3d vec = distanceVec.normalize().scale(blastLevel / 4.5F);
                if(distanceVec.length() > 4)
                    vec = vec.scale(1F / 3.3F);
                if(getProjectileType() == PotatoCannonProjectileTypes.CHOCOLATE_BERRIES || getProjectileType() == PotatoCannonProjectileTypes.SWEET_BERRIES)
                    vec = vec.scale(1F / 2F);
                vec = vec.scale(AllConfigs.SERVER.blastEnchantPowerMultiplier.getF());
                entity.push(vec.x, vec.y, vec.z);
            });
            for(int x = 0; x < 15; x++) {
                Vector3d motion = VecHelper.offsetRandomly(Vector3d.ZERO, level.random, 0.3F);
                level.addParticle(new AirFlowParticleData((int)getX(), (int)getY(), (int)getZ()), getX(), getY(), getZ(), motion.x, motion.y, motion.z);
            }
        }
        super.remove();
    }
    
    private static final String BLAST_AMOUNT = "blast_amount";
    public void readAdditionalSaveData(CompoundNBT nbt) {
        blastLevel = nbt.getInt(BLAST_AMOUNT);
        super.readAdditionalSaveData(nbt);
    }
    
    public void addAdditionalSaveData(CompoundNBT nbt) {
        nbt.putInt(BLAST_AMOUNT, blastLevel);
        super.addAdditionalSaveData(nbt);
    }
    
}
