package com.mystic.eclipse.init;

import com.mystic.eclipse.blocks.*;
import com.mystic.eclipse.creativetab.EclipseGroup;
import com.mystic.eclipse.utils.Reference;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.function.Function;

public class BlockInit {
    public static void init() {
        registerBlockTintColors();
    }
    //Tint Colors
    public static void registerBlockTintColors() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x707070, DARK_DIRT_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0xCDCDCD, LIGHT_DIRT_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x929292, TWILIGHT_DIRT_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x707070, DARK_GRASS_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0xCDCDCD,  LIGHT_GRASS_BLOCK);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> 0x929292, TWILIGHT_GRASS_BLOCK);

    }
    //Blocks
    public static final DarkStoneBlock DARK_STONE_BLOCK = (DarkStoneBlock) register("dark_stone", new DarkStoneBlock(FabricBlockSettings.create()));
    public static final LightStoneBlock LIGHT_STONE_BLOCK = (LightStoneBlock) register("light_stone", new LightStoneBlock(FabricBlockSettings.create()));
    public static final TwilightStoneBlock TWILIGHT_STONE_BLOCK = (TwilightStoneBlock) register("twilight_stone", new TwilightStoneBlock(FabricBlockSettings.create()));
    public static final DarkDirtBlock DARK_DIRT_BLOCK = (DarkDirtBlock) register("dark_dirt", new DarkDirtBlock(FabricBlockSettings.create()));
    public static final LightDirtBlock LIGHT_DIRT_BLOCK = (LightDirtBlock) register("light_dirt", new LightDirtBlock(FabricBlockSettings.create()));
    public static final TwilightDirtBlock TWILIGHT_DIRT_BLOCK = (TwilightDirtBlock) register("twilight_dirt", new TwilightDirtBlock(FabricBlockSettings.create()));
    public static final DarkGrassBlock DARK_GRASS_BLOCK = (DarkGrassBlock) register("dark_grass_block", new DarkGrassBlock(FabricBlockSettings.create()));
    public static final LightGrassBlock LIGHT_GRASS_BLOCK = (LightGrassBlock) register("light_grass_block", new LightGrassBlock(FabricBlockSettings.create()));
    public static final TwilightGrassBlock TWILIGHT_GRASS_BLOCK = (TwilightGrassBlock) register("twilight_grass_block", new TwilightGrassBlock(FabricBlockSettings.create()));

    private static Block baseRegister(String name, Block block, Function<Block, Item> item) {
        Registry.register(Registries.BLOCK, new Identifier(Reference.MODID, name), block);
        register(name, item.apply(block));
        EclipseGroup.addToMainTab(block.asItem());
        return block;
    }

    private static Block register(String name, Block block) {
        return baseRegister(name, block, BlockInit::registerBlockItem);
    }

    private static Block blockOnlyRegistry(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(Reference.MODID, name), block);
    }

    private static BlockItem registerBlockItem(Block block) {
        return new BlockItem(Objects.requireNonNull(block), new Item.Settings());
    }

    public static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Reference.MODID, name), item);
    }
}
