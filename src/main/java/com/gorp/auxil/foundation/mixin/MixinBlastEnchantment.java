package com.gorp.auxil.foundation.mixin;

import com.gorp.auxil.AllEnchantments;
import com.gorp.auxil.AllEntityTypes;
import com.gorp.auxil.content.curiosities.BlastingPotatoProjectileEntity;
import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.curiosities.armor.BackTankUtil;
import com.simibubi.create.content.curiosities.weapons.PotatoCannonItem;
import com.simibubi.create.content.curiosities.weapons.PotatoCannonPacket;
import com.simibubi.create.content.curiosities.weapons.PotatoCannonProjectileTypes;
import com.simibubi.create.content.curiosities.zapper.ShootableGadgetItemMethods;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PotatoCannonItem.class)
public class MixinBlastEnchantment {
    @Shadow
    public static ItemStack CLIENT_CURRENT_AMMO;
    @Final
    @Shadow
    public static final int MAX_DAMAGE = 100;
    
    @Inject(at = @At("HEAD"), method = "use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", cancellable = true, remap = false)
    public void use (World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if(EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.POTATO_BLAST.get(), stack) > 0) {
            PotatoCannonItem item = ((PotatoCannonItem) stack.getItem());
            Optional<ActionResult> optional = (MixinBlastEnchantment.findAmmoInInventoryMixin(world, player, stack).map((itemStack) -> {
                if (ShootableGadgetItemMethods.shouldSwap(player, stack, hand, MixinBlastEnchantment::isCannonMixin)) {
                    return ActionResult.fail(stack);
                } else if (world.isClientSide) {
                    CreateClient.POTATO_CANNON_RENDER_HANDLER.dontAnimateItem(hand);
                    return ActionResult.success(stack);
                } else {
                    Vector3d barrelPos = ShootableGadgetItemMethods.getGunBarrelVec(player, hand == Hand.MAIN_HAND, new Vector3d(0.75D, -0.15000000596046448D, 1.5D));
                    Vector3d correction = ShootableGadgetItemMethods.getGunBarrelVec(player, hand == Hand.MAIN_HAND, new Vector3d(-0.05000000074505806D, 0.0D, 0.0D)).subtract(player.position().add(0.0D, player.getEyeHeight(), 0.0D));
                    PotatoCannonProjectileTypes projectileType = PotatoCannonProjectileTypes.getProjectileTypeOf(itemStack).orElse(PotatoCannonProjectileTypes.FALLBACK);
                    Vector3d lookVec = player.getLookAngle();
                    Vector3d motion = lookVec.add(correction).normalize().scale(projectileType.getVelocityMultiplier());
                    float soundPitch = projectileType.getSoundPitch() + (Create.RANDOM.nextFloat() - 0.5F) / 4.0F;
                    boolean spray = projectileType.getSplit() > 1;
                    Vector3d sprayBase = VecHelper.rotate(new Vector3d(0.0D, 0.1D, 0.0D), 360.0F * Create.RANDOM.nextFloat(), Direction.Axis.Z);
                    float sprayChange = 360.0F / (float) projectileType.getSplit();
        
                    for (int i = 0; i < projectileType.getSplit(); ++i) {
                        BlastingPotatoProjectileEntity projectile = AllEntityTypes.BLASTING_PROJECTILE.create(world);
                        projectile.setBlastLevel(EnchantmentHelper.getItemEnchantmentLevel(AllEnchantments.POTATO_BLAST.get(), stack));
                        projectile.setItem(itemStack);
                        projectile.setEnchantmentEffectsFromCannon(stack);
                        Vector3d splitMotion = motion;
                        if (spray) {
                            float imperfection = 40.0F * (Create.RANDOM.nextFloat() - 0.5F);
                            Vector3d sprayOffset = VecHelper.rotate(sprayBase, (float) i * sprayChange + imperfection, Direction.Axis.Z);
                            splitMotion = motion.add(VecHelper.lookAt(sprayOffset, motion));
                        }
            
                        projectile.setPos(barrelPos.x, barrelPos.y, barrelPos.z);
                        projectile.setDeltaMovement(splitMotion);
                        projectile.setOwner(player);
                        world.addFreshEntity(projectile);
                    }
        
                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                        if (itemStack.isEmpty()) {
                            player.inventory.removeItem(itemStack);
                        }
                    }
        
                    if (!BackTankUtil.canAbsorbDamage(player, MixinBlastEnchantment.maxUsesMixin())) {
                        stack.hurtAndBreak(1, player, (p) -> {
                            p.broadcastBreakEvent(hand);
                        });
                    }
        
                    Optional<ItemStack> op = MixinBlastEnchantment.findAmmoInInventoryMixin(world, player, stack);
                    if(op.isPresent()) {
                        Integer cooldown = op.flatMap(PotatoCannonProjectileTypes::getProjectileTypeOf).map(PotatoCannonProjectileTypes::getReloadTicks).orElse(10);
                        ShootableGadgetItemMethods.applyCooldown(player, stack, hand, MixinBlastEnchantment::isCannonMixin, cooldown);
                        ShootableGadgetItemMethods.sendPackets(player, (b) -> {
                            return new PotatoCannonPacket(barrelPos, lookVec.normalize(), itemStack, hand, soundPitch, b);
                        });
                    }
                    return ActionResult.success(stack);
                }
            }));
            optional.ifPresent(cir::setReturnValue);
        }
    }
    
    private static int maxUsesMixin() {
        return AllConfigs.SERVER.curiosities.maxPotatoCannonShots.get();
    }
    
    private static boolean isCannonMixin(ItemStack stack) {
        return stack.getItem() instanceof PotatoCannonItem;
    }
    
    private static Optional<ItemStack> findAmmoInInventoryMixin(World world, PlayerEntity player, ItemStack held) {
        ItemStack findAmmo = player.getProjectile(held);
        return PotatoCannonProjectileTypes.getProjectileTypeOf(findAmmo).map(($) -> {
            return findAmmo;
        });
    }
}
