package com.mystic.eclipse.mixin;

import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.LightStorage;
import net.minecraft.world.chunk.light.SkyLightStorage;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkyLightStorage.class)
public abstract class SkylightMixin extends LightStorage<SkyLightStorage.Data> {

    @Shadow public abstract boolean isSectionEnabled(long sectionPos);

    public SkylightMixin(LightType lightType, ChunkProvider chunkProvider, SkyLightStorage.Data lightData) {
        super(lightType, chunkProvider, lightData);
    }

    @Inject(at = @At("HEAD"), method = "method_31931", cancellable = true)
    public void lightingOnX(long l, boolean bl, CallbackInfoReturnable<Integer> cir){
        cir.cancel();
        if(EclipseDimension.isEclipseDimension((World) chunkProvider.getWorld())) {
            if (!((World) chunkProvider.getWorld()).isClient) {
                ServerWorld serverWorld = (ServerWorld) chunkProvider.getWorld();
                for (ServerPlayerEntity player : serverWorld.getServer().getPlayerManager().getPlayerList()) {
                    double x = player.getX();
                    if (x < -16) {
                        lightingX(l, bl, cir, 0, x);
                    } else if (x > -16 && x < 16) {
                        lightingX(l, bl, cir, 7, x);
                    } else {
                        lightingX(l, bl, cir, 15, x);
                    }
                }
            } else {
                double x = MinecraftClient.getInstance().player.getX();
                if (x < -16) {
                    lightingX(l, bl, cir, 0, x);
                } else if (x > -16 && x < 16) {
                    lightingX(l, bl, cir, 7, x);
                } else {
                    lightingX(l, bl, cir, 15, x);
                }
            }
        } else{
            lightingX(l, bl, cir, 15, 0);
        }
    }

    public void lightingX(long l, boolean bl,  CallbackInfoReturnable<Integer> cir, int level, double x){
        long m = ChunkSectionPos.fromBlockPos(l);
        int i = ChunkSectionPos.unpackY(m);
        SkyLightStorage.Data data = bl ? storage : uncachedStorage;
        int j = data.columnToTopSection.get(ChunkSectionPos.withZeroY(m));
        if (j != data.minSectionY && i < j) {
            ChunkNibbleArray chunkNibbleArray = this.getLightSection(data, m);
            if (chunkNibbleArray == null) {
                for(l = BlockPos.removeChunkSectionLocalY(l); chunkNibbleArray == null; chunkNibbleArray = this.getLightSection(data, m)) {
                    ++i;
                    if (i >= j) {
                        cir.setReturnValue(level);
                    }

                    l = BlockPos.add(l, 0, 16, 0);
                    m = ChunkSectionPos.offset(m, Direction.UP);
                }
            }
            int lightLevelDynamic = chunkNibbleArray.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l)));
            if(EclipseDimension.isEclipseDimension((World) chunkProvider.getWorld())) {
                /*if (x < -16) {
                   cir.setReturnValue(lightLevelDynamic - lightLevelDynamic);
                } else if (x > -16 && x < 16) {
                   cir.setReturnValue(lightLevelDynamic / 2);
                } else {
                   cir.setReturnValue(lightLevelDynamic);
                }*/
            } else {
                cir.setReturnValue(lightLevelDynamic);
            }
        } else {
            cir.setReturnValue(bl && !isSectionEnabled(m) ? 0 : level);
        }
    }
}
