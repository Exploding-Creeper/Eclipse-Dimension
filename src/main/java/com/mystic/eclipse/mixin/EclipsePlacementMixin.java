package com.mystic.eclipse.mixin;

import com.mystic.eclipse.world.EclipsePlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class EclipsePlacementMixin {
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), cancellable = true)
    private void eclipse$rewritePlacement(BlockPos pos, BlockState state, int flags, int recursion, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level) (Object) this;
        BlockState rewritten = EclipsePlacement.rewrite(level, pos, state);
        if (rewritten == state) {
            return;
        }
        if (rewritten == null) {
            cir.setReturnValue(false);
            return;
        }
        cir.setReturnValue(level.setBlock(pos, rewritten, flags, recursion));
    }
}
