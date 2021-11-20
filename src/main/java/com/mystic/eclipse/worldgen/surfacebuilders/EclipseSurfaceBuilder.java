package com.mystic.eclipse.worldgen.surfacebuilders;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.Random;

public class EclipseSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
    public EclipseSurfaceBuilder(Codec<TernarySurfaceConfig> codec) {
        super(codec);
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int x, int z, int terrainHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, int i, long seed, TernarySurfaceConfig config) {
        this.buildSurface(random, chunk, biome, x, z, terrainHeight, noise, defaultBlock, defaultFluid, config.getTopMaterial(), config.getUnderMaterial(), config.getUnderwaterMaterial(), seaLevel);
    }

    public static void buildSurface(Random random, Chunk chunk, Biome biome, int x, int z, int terrainHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, BlockState topBlock, BlockState middleBlock, BlockState underwaterBlock, int seaLevel) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int depth = -1; // Will be used to know how deep we are in solid blocks so we know when to stop placing middleBlock
        int scaledNoise = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);

        // Start at top land and loop downward
        for(int y = terrainHeight; y >= 0; --y) {

            // Get the block in the world (Overworld will always give Air, Water, or Stone)
            mutable.set(x, y, z);
            BlockState currentBlockInWorld = chunk.getBlockState(mutable);

            // Reset the depth counter as we are not in land anymore
            if (currentBlockInWorld.isAir()) {
                depth = -1;
            }

            // Else if we are at liquid, we can use this to swap the default fluid in your biome
            else if (!currentBlockInWorld.getFluidState().isEmpty()) {
                chunk.setBlockState(mutable,defaultFluid, false);
            }

            // We are in solid land now. Skip Bedrock as we shouldn't replace that
            else if (currentBlockInWorld.getBlock() != Blocks.BEDROCK) {
                // -1 depth means we are switching from air to solid land. Place the surface block now
                if (depth == -1) {
                    // Signal that depth is now on the surface so we can start placing middle blocks when moving down next loop.
                    depth = 0;

                    // The typical normal dry surface of the biome.
                    if(y >= seaLevel - 1){
                        chunk.setBlockState(mutable, topBlock, false);
                    }
                    // Places middle block when starting to go under sealevel.
                    // Think of this as the top block of the bottom of shallow lakes in your biome.
                    else if(y >= seaLevel - scaledNoise - 7){
                        chunk.setBlockState(mutable, middleBlock, false);
                    }
                    // Places the underwater block when really deep under sealevel instead.
                    // This is like the top block of the sea floor.
                    else{
                        chunk.setBlockState(mutable, underwaterBlock, false);
                    }
                }
                // Place block only when under surface and down to as deep as the scaledNoise says to go.
                else if (depth <= scaledNoise) {
                    // Increment depth to keep track of how deep we have gone
                    depth++;
                    chunk.setBlockState(mutable, middleBlock, false);
                }
                // Place the default block if not placing top, middle, or underwater block anymore.
                else {
                    chunk.setBlockState(mutable, defaultBlock, false);
                }
            }
        }
    }
}
