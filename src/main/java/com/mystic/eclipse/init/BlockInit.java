package com.mystic.eclipse.init;

import com.mystic.eclipse.blocks.EclipseArdorBlock;
import com.mystic.eclipse.blocks.NyxHumusBlock;
import com.mystic.eclipse.blocks.EclipseFundamentumBlock;
import com.mystic.eclipse.blocks.NyxSeroBlock;
import com.mystic.eclipse.blocks.NyxSaxumBlock;
import com.mystic.eclipse.blocks.SolHumusBlock;
import com.mystic.eclipse.blocks.SolSeroBlock;
import com.mystic.eclipse.blocks.SolSaxumBlock;
import com.mystic.eclipse.blocks.LuxHumusBlock;
import com.mystic.eclipse.blocks.LuxSeroBlock;
import com.mystic.eclipse.blocks.LuxSaxumBlock;
import com.mystic.eclipse.creativetab.EclipseGroup;
import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.trees.EclipseTreeGrower;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MODID);
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MODID);

    public static final RegistryObject<EclipseFundamentumBlock> NYX_FUNDAMENTUM = register("nyx_fundamentum", () -> new EclipseFundamentumBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<EclipseFundamentumBlock> SOL_FUNDAMENTUM = register("sol_fundamentum", () -> new EclipseFundamentumBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<EclipseFundamentumBlock> LUX_FUNDAMENTUM = register("lux_fundamentum", () -> new EclipseFundamentumBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<NyxSaxumBlock> NYX_SAXUM = register("nyx_saxum", () -> new NyxSaxumBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<SolSaxumBlock> SOL_SAXUM = register("sol_saxum", () -> new SolSaxumBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<LuxSaxumBlock> LUX_SAXUM = register("lux_saxum", () -> new LuxSaxumBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<NyxHumusBlock> NYX_HUMUS = register("nyx_humus", () -> new NyxHumusBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<SolHumusBlock> SOL_HUMUS = register("sol_humus", () -> new SolHumusBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<LuxHumusBlock> LUX_HUMUS = register("lux_humus", () -> new LuxHumusBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<NyxSeroBlock> NYX_SERO = register("nyx_sero", () -> new NyxSeroBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<SolSeroBlock> SOL_SERO = register("sol_sero", () -> new SolSeroBlock(BlockBehaviour.Properties.of()));
    public static final RegistryObject<LuxSeroBlock> LUX_SERO = register("lux_sero", () -> new LuxSeroBlock(BlockBehaviour.Properties.of()));

    public static final RegistryObject<RotatedPillarBlock> NYX_TRUNCUS = register("nyx_truncus", () -> new RotatedPillarBlock(truncusProperties()));
    public static final RegistryObject<LeavesBlock> NYX_FOLIUM = register("nyx_folium", () -> new LeavesBlock(foliumProperties()));
    public static final RegistryObject<SaplingBlock> NYX_SURCULUS = register("nyx_surculus", () -> new SaplingBlock(EclipseTreeGrower.NYX_UMBRA, surculusProperties()));

    public static final RegistryObject<RotatedPillarBlock> SOL_TRUNCUS = register("sol_truncus", () -> new RotatedPillarBlock(truncusProperties()));
    public static final RegistryObject<LeavesBlock> SOL_FOLIUM = register("sol_folium", () -> new LeavesBlock(foliumProperties()));
    public static final RegistryObject<SaplingBlock> SOL_SURCULUS = register("sol_surculus", () -> new SaplingBlock(EclipseTreeGrower.SOL_LUMI, surculusProperties()));

    public static final RegistryObject<RotatedPillarBlock> LUX_TRUNCUS = register("lux_truncus", () -> new RotatedPillarBlock(truncusProperties()));
    public static final RegistryObject<LeavesBlock> LUX_FOLIUM = register("lux_folium", () -> new LeavesBlock(foliumProperties()));
    public static final RegistryObject<SaplingBlock> LUX_SURCULUS = register("lux_surculus", () -> new SaplingBlock(EclipseTreeGrower.LUX_ECLIPSO, surculusProperties()));

    public static final RegistryObject<Block> FILUM = registerHidden("filum", () -> new Block(BlockBehaviour.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.COLOR_BLACK).strength(1.5F, 6.0F).sound(SoundType.METAL)));

    public static final RegistryObject<EclipseArdorBlock> NYX_ARDOR = registerBlock("nyx_ardor", () -> new EclipseArdorBlock(7));
    public static final RegistryObject<EclipseArdorBlock> SOL_ARDOR = registerBlock("sol_ardor", () -> new EclipseArdorBlock(15));
    public static final RegistryObject<EclipseArdorBlock> LUX_ARDOR = registerBlock("lux_ardor", () -> new EclipseArdorBlock(11));

    private static BlockBehaviour.Properties truncusProperties() {
        return BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2.0F);
    }

    private static BlockBehaviour.Properties foliumProperties() {
        return BlockBehaviour.Properties.of()
                .strength(0.2F)
                .randomTicks()
                .sound(SoundType.GRASS)
                .noOcclusion()
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false);
    }

    private static BlockBehaviour.Properties surculusProperties() {
        return BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.GRASS);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        RegistryObject<T> registered = registerHidden(name, block);
        EclipseGroup.addToMainTab(registered::get);
        return registered;
    }

    private static <T extends Block> RegistryObject<T> registerHidden(String name, Supplier<T> block) {
        RegistryObject<T> registered = BLOCKS.register(name, block);
        BLOCK_ITEMS.register(name, () -> new BlockItem(registered.get(), new Item.Properties()));
        return registered;
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
}
