package com.mystic.eclipse.worldgen.biomes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;

/**
 * Nyx, Sol, and Lux are placed by the vanilla noise biome source.
 * Light follows whichever biome is actually at the position.
 */
public final class EclipseZones {
    public static final ResourceKey<Biome> NYX = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("eclipse", "nyx_umbra"));
    public static final ResourceKey<Biome> SOL = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("eclipse", "sol_lumi"));
    public static final ResourceKey<Biome> LUX = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("eclipse", "lux_eclipso"));

    private static final int PATCH = 150;

    private EclipseZones() {
    }

    public static String family(Holder<Biome> biome) {
        if (biome == null) {
            return "nyx";
        }
        return biome.unwrapKey().map(key -> {
            String path = key.location().getPath();
            if (path.startsWith("sol")) {
                return "sol";
            }
            if (path.startsWith("lux")) {
                return "lux";
            }
            return "nyx";
        }).orElse("nyx");
    }

    public static int lightLevel(LevelAccessor level, BlockPos pos) {
        return switch (family(level.getBiome(pos))) {
            case "lux" -> 7;
            case "sol" -> 15;
            default -> 0;
        };
    }

    public static float cloudShade(LevelAccessor level, BlockPos pos) {
        return switch (family(level.getBiome(pos))) {
            case "lux" -> 0.45F;
            case "sol" -> 1.0F;
            default -> 0.0F;
        };
    }

    public static float sunStrength(LevelAccessor level, BlockPos pos) {
        return switch (family(level.getBiome(pos))) {
            case "lux" -> 0.55F;
            case "sol" -> 1.0F;
            default -> 0.28F;
        };
    }

    public static boolean isLightSide(int blockX, int blockZ) {
        int warpedX = blockX + edgeShift(blockX, blockZ, 0);
        int warpedZ = blockZ + edgeShift(blockX, blockZ, 1);
        int cellX = Math.floorDiv(warpedX, PATCH);
        int cellZ = Math.floorDiv(warpedZ, PATCH);
        return ((cellX + cellZ) & 1) == 0;
    }

    public static boolean isTwilightOasis(int blockX, int blockZ) {
        return oasisNoise(blockX, blockZ) > 0.80;
    }

    /**
     * Dark patches stay at 0 and light patches stay at 15, at every height.
     * A twilight oasis eases to 7.
     */
    public static int lightLevel(int blockX, int blockZ) {
        double fade = (oasisNoise(blockX, blockZ) - 0.68) / 0.14;
        if (fade < 0.0) {
            fade = 0.0;
        } else if (fade > 1.0) {
            fade = 1.0;
        }
        fade = fade * fade * (3.0 - 2.0 * fade);
        if (isLightSide(blockX, blockZ)) {
            return (int) Math.round(15.0 + (7.0 - 15.0) * fade);
        }
        return (int) Math.round(7.0 * fade);
    }

    private static int edgeShift(int blockX, int blockZ, int salt) {
        double n = smoothValue(blockX + salt * 12000, blockZ - salt * 5000, 160);
        return (int) Math.round((n - 0.5) * 50.0);
    }

    private static double oasisNoise(int blockX, int blockZ) {
        double patch = smoothValue(blockX + 900, blockZ + 400, 64);
        double shape = smoothValue(blockX - 2200, blockZ + 80, 22);
        return patch * 0.7 + shape * 0.3;
    }

    private static double smoothValue(int blockX, int blockZ, int cell) {
        int x0 = Math.floorDiv(blockX, cell);
        int z0 = Math.floorDiv(blockZ, cell);
        double fx = (blockX - (double) x0 * cell) / cell;
        double fz = (blockZ - (double) z0 * cell) / cell;
        fx = fx * fx * (3.0 - 2.0 * fx);
        fz = fz * fz * (3.0 - 2.0 * fz);
        double n00 = hash(x0, z0);
        double n10 = hash(x0 + 1, z0);
        double n01 = hash(x0, z0 + 1);
        double n11 = hash(x0 + 1, z0 + 1);
        double nx0 = n00 + (n10 - n00) * fx;
        double nx1 = n01 + (n11 - n01) * fx;
        return nx0 + (nx1 - nx0) * fz;
    }

    private static double hash(int x, int z) {
        long n = (x * 374761393L) ^ (z * 668265263L);
        n = (n ^ (n >> 13)) * 1274126177L;
        n ^= n >> 16;
        return (n & 0xFFFFFFL) / 16777215.0;
    }
}
