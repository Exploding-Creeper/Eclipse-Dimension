package com.mystic.eclipse.mixin;

import com.mystic.eclipse.world.EclipsePlacement;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class EclipseBucketMixin {
    @Shadow
    protected abstract void playEmptySound(Player player, LevelAccessor level, BlockPos pos);

    /**
     * Forge mob buckets store the fluid in {@code fluidSupplier} and leave {@code content} null.
     * A coremod redirects {@code BucketItem}'s own field reads to {@code getFluid()}, but a mixin
     * field read does not. Reading the field made every fish bucket look empty, so the refusal
     * never ran, placement was rejected, and {@code checkExtraContent} never released the mob.
     */
    @Inject(method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void eclipse$refuseForeignFluid(Player player, Level level, BlockPos pos, BlockHitResult hit, ItemStack container, CallbackInfoReturnable<Boolean> cir) {
        Fluid fluid = ((BucketItem) (Object) this).getFluid();
        boolean inside = EclipseDimension.isEclipseDimension(level);
        boolean eclipseFluid = EclipsePlacement.isEclipseFluid(fluid);
        if (fluid == null || fluid == Fluids.EMPTY || eclipseFluid == inside) {
            return;
        }
        EclipsePlacement.voidParticles(level, pos);
        if (!carriesEntity(container)) {
            cir.setReturnValue(false);
            return;
        }
        // Skip the fluid. Returning true lets BucketItem.use release the mob and empty the bucket.
        this.playEmptySound(player, level, pos);
        cir.setReturnValue(true);
    }

    private boolean carriesEntity(ItemStack stack) {
        if ((Object) this instanceof MobBucketItem) {
            return true;
        }
        if (stack != null) {
            CompoundTag tag = stack.getTag();
            if (tag != null && (tag.contains("EntityTag") || tag.contains("Entity") || tag.contains("entity"))) {
                return true;
            }
        }
        Class<?> type = this.getClass();
        while (type != null && type != Object.class) {
            String name = type.getSimpleName().toLowerCase(Locale.ROOT);
            if (name.contains("mobbucket") || name.contains("entitybucket")) {
                return true;
            }
            type = type.getSuperclass();
        }
        return false;
    }
}
