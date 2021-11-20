package com.mystic.eclipse.init;

import com.mystic.eclipse.blocks.DarkStoneBlock;
import com.mystic.eclipse.blocks.LightStoneBlock;
import com.mystic.eclipse.blocks.TwilightStoneBlock;
import com.mystic.eclipse.utils.Reference;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Objects;
import java.util.function.Function;

public class BlockInit {
    public static void init() {}
    public static final DarkStoneBlock DARK_STONE_BLOCK = (DarkStoneBlock) register("dark_stone", new DarkStoneBlock(FabricBlockSettings.of(Material.STONE)));
    public static final LightStoneBlock LIGHT_STONE_BLOCK = (LightStoneBlock) register("light_stone", new LightStoneBlock(FabricBlockSettings.of(Material.STONE)));
    public static final TwilightStoneBlock TWILIGHT_STONE_BLOCK = (TwilightStoneBlock) register("twilight_stone", new TwilightStoneBlock(FabricBlockSettings.of(Material.STONE)));

    private static Block baseRegister(String name, Block block, Function<Block, Item> item) {
        Registry.register(Registry.BLOCK, new Identifier(Reference.MODID, name), block);
        register(name, item.apply(block));
        return block;
    }

    private static Block register(String name, Block block) {
        return baseRegister(name, block, BlockInit::registerBlockItem);
    }

    private static Block blockOnlyRegistry(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(Reference.MODID, name), block);
    }

    private static BlockItem registerBlockItem(Block block) {
        return new BlockItem(Objects.requireNonNull(block), new Item.Settings());
    }

    public static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Reference.MODID, name), item);
    }
}
