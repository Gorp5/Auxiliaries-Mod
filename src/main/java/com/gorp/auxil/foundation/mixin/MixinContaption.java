package com.gorp.auxil.foundation.mixin;

import com.gorp.auxil.foundation.networking.ShadowChassisAreaHandler;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Contraption.class)
public class MixinContaption {
    @Inject(at = @At("HEAD"), method = "movementAllowed(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true, remap = false)
    protected void movementAllowed(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(ShadowChassisAreaHandler.containsBlock(pos))
            cir.setReturnValue(false);
    }
}