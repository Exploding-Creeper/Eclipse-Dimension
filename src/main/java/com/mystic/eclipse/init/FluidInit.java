package com.mystic.eclipse.init;

import com.mystic.eclipse.EclipseMain;
import com.mystic.eclipse.creativetab.EclipseGroup;
import com.mystic.eclipse.fluids.EclipseFluidType;
import com.mystic.eclipse.fluids.EclipseIgnisFluid;
import com.mystic.eclipse.utils.Reference;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FluidInit {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Reference.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Reference.MODID);
    public static final DeferredRegister<Block> FLUID_BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MODID);
    public static final DeferredRegister<Item> BUCKETS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MODID);

    public static final RegistryObject<FluidType> NYX_UNDA_TYPE = undaType("nyx_unda", 0x99323238, 0);
    public static final RegistryObject<FluidType> SOL_UNDA_TYPE = undaType("sol_unda", 0x99E4E4EA, 0);
    public static final RegistryObject<FluidType> LUX_UNDA_TYPE = undaType("lux_unda", 0x998A8A96, 0);
    public static final RegistryObject<FluidType> NYX_IGNIS_TYPE = ignisType("nyx_ignis", 0x993C3C44, 7);
    public static final RegistryObject<FluidType> SOL_IGNIS_TYPE = ignisType("sol_ignis", 0x99F2F2EC, 15);
    public static final RegistryObject<FluidType> LUX_IGNIS_TYPE = ignisType("lux_ignis", 0x99A0A0AA, 11);

    public static final RegistryObject<ForgeFlowingFluid> NYX_UNDA = FLUIDS.register("nyx_unda", () -> new ForgeFlowingFluid.Source(nyxUndaProperties()));
    public static final RegistryObject<ForgeFlowingFluid> NYX_UNDA_FLOWING = FLUIDS.register("nyx_unda_flowing", () -> new ForgeFlowingFluid.Flowing(nyxUndaProperties()));
    public static final RegistryObject<ForgeFlowingFluid> SOL_UNDA = FLUIDS.register("sol_unda", () -> new ForgeFlowingFluid.Source(solUndaProperties()));
    public static final RegistryObject<ForgeFlowingFluid> SOL_UNDA_FLOWING = FLUIDS.register("sol_unda_flowing", () -> new ForgeFlowingFluid.Flowing(solUndaProperties()));
    public static final RegistryObject<ForgeFlowingFluid> LUX_UNDA = FLUIDS.register("lux_unda", () -> new ForgeFlowingFluid.Source(luxUndaProperties()));
    public static final RegistryObject<ForgeFlowingFluid> LUX_UNDA_FLOWING = FLUIDS.register("lux_unda_flowing", () -> new ForgeFlowingFluid.Flowing(luxUndaProperties()));

    public static final RegistryObject<ForgeFlowingFluid> NYX_IGNIS = FLUIDS.register("nyx_ignis", () -> new EclipseIgnisFluid.Source(nyxIgnisProperties(), BlockInit.NYX_ARDOR));
    public static final RegistryObject<ForgeFlowingFluid> NYX_IGNIS_FLOWING = FLUIDS.register("nyx_ignis_flowing", () -> new EclipseIgnisFluid.Flowing(nyxIgnisProperties(), BlockInit.NYX_ARDOR));
    public static final RegistryObject<ForgeFlowingFluid> SOL_IGNIS = FLUIDS.register("sol_ignis", () -> new EclipseIgnisFluid.Source(solIgnisProperties(), BlockInit.SOL_ARDOR));
    public static final RegistryObject<ForgeFlowingFluid> SOL_IGNIS_FLOWING = FLUIDS.register("sol_ignis_flowing", () -> new EclipseIgnisFluid.Flowing(solIgnisProperties(), BlockInit.SOL_ARDOR));
    public static final RegistryObject<ForgeFlowingFluid> LUX_IGNIS = FLUIDS.register("lux_ignis", () -> new EclipseIgnisFluid.Source(luxIgnisProperties(), BlockInit.LUX_ARDOR));
    public static final RegistryObject<ForgeFlowingFluid> LUX_IGNIS_FLOWING = FLUIDS.register("lux_ignis_flowing", () -> new EclipseIgnisFluid.Flowing(luxIgnisProperties(), BlockInit.LUX_ARDOR));

    public static final RegistryObject<LiquidBlock> NYX_UNDA_BLOCK = fluidBlock("nyx_unda", NYX_UNDA, MapColor.COLOR_BLACK, 0);
    public static final RegistryObject<LiquidBlock> SOL_UNDA_BLOCK = fluidBlock("sol_unda", SOL_UNDA, MapColor.COLOR_LIGHT_GRAY, 0);
    public static final RegistryObject<LiquidBlock> LUX_UNDA_BLOCK = fluidBlock("lux_unda", LUX_UNDA, MapColor.COLOR_GRAY, 0);
    public static final RegistryObject<LiquidBlock> NYX_IGNIS_BLOCK = fluidBlock("nyx_ignis", NYX_IGNIS, MapColor.COLOR_BLACK, 7);
    public static final RegistryObject<LiquidBlock> SOL_IGNIS_BLOCK = fluidBlock("sol_ignis", SOL_IGNIS, MapColor.SNOW, 15);
    public static final RegistryObject<LiquidBlock> LUX_IGNIS_BLOCK = fluidBlock("lux_ignis", LUX_IGNIS, MapColor.COLOR_GRAY, 11);

    public static final RegistryObject<Item> NYX_UNDA_BUCKET = bucket("nyx_unda_bucket", NYX_UNDA);
    public static final RegistryObject<Item> SOL_UNDA_BUCKET = bucket("sol_unda_bucket", SOL_UNDA);
    public static final RegistryObject<Item> LUX_UNDA_BUCKET = bucket("lux_unda_bucket", LUX_UNDA);
    public static final RegistryObject<Item> NYX_IGNIS_BUCKET = bucket("nyx_ignis_bucket", NYX_IGNIS);
    public static final RegistryObject<Item> SOL_IGNIS_BUCKET = bucket("sol_ignis_bucket", SOL_IGNIS);
    public static final RegistryObject<Item> LUX_IGNIS_BUCKET = bucket("lux_ignis_bucket", LUX_IGNIS);

    private static RegistryObject<FluidType> undaType(String name, int tint, int light) {
        return FLUID_TYPES.register(name, () -> new EclipseFluidType(FluidType.Properties.create()
                .descriptionId("block.eclipse." + name)
                .canSwim(true)
                .canDrown(true)
                .canExtinguish(true)
                .canHydrate(true)
                .canPushEntity(true)
                .supportsBoating(true)
                .canConvertToSource(true)
                .lightLevel(light)
                .density(1000)
                .viscosity(1000)
                .temperature(300)
                .motionScale(0.014)
                .fallDistanceModifier(0.0F)
                .pathType(BlockPathTypes.WATER)
                .adjacentPathType(BlockPathTypes.WATER_BORDER)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY),
                EclipseMain.id("block/" + name + "_still"),
                EclipseMain.id("block/" + name + "_flow"),
                tint));
    }

    private static RegistryObject<FluidType> ignisType(String name, int tint, int light) {
        return FLUID_TYPES.register(name, () -> new EclipseFluidType(FluidType.Properties.create()
                .descriptionId("block.eclipse." + name)
                .canSwim(true)
                .canDrown(false)
                .canExtinguish(false)
                .canPushEntity(true)
                .supportsBoating(false)
                .canConvertToSource(false)
                .lightLevel(light)
                .density(1400)
                .viscosity(1800)
                .temperature(300)
                .motionScale(0.011)
                .fallDistanceModifier(0.0F)
                .pathType(BlockPathTypes.WATER)
                .adjacentPathType(BlockPathTypes.WATER_BORDER)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA),
                EclipseMain.id("block/" + name + "_still"),
                EclipseMain.id("block/" + name + "_flow"),
                tint));
    }

    private static ForgeFlowingFluid.Properties nyxUndaProperties() {
        return unda(NYX_UNDA_TYPE, NYX_UNDA, NYX_UNDA_FLOWING, NYX_UNDA_BLOCK, NYX_UNDA_BUCKET);
    }

    private static ForgeFlowingFluid.Properties solUndaProperties() {
        return unda(SOL_UNDA_TYPE, SOL_UNDA, SOL_UNDA_FLOWING, SOL_UNDA_BLOCK, SOL_UNDA_BUCKET);
    }

    private static ForgeFlowingFluid.Properties luxUndaProperties() {
        return unda(LUX_UNDA_TYPE, LUX_UNDA, LUX_UNDA_FLOWING, LUX_UNDA_BLOCK, LUX_UNDA_BUCKET);
    }

    private static ForgeFlowingFluid.Properties nyxIgnisProperties() {
        return ignis(NYX_IGNIS_TYPE, NYX_IGNIS, NYX_IGNIS_FLOWING, NYX_IGNIS_BLOCK, NYX_IGNIS_BUCKET);
    }

    private static ForgeFlowingFluid.Properties solIgnisProperties() {
        return ignis(SOL_IGNIS_TYPE, SOL_IGNIS, SOL_IGNIS_FLOWING, SOL_IGNIS_BLOCK, SOL_IGNIS_BUCKET);
    }

    private static ForgeFlowingFluid.Properties luxIgnisProperties() {
        return ignis(LUX_IGNIS_TYPE, LUX_IGNIS, LUX_IGNIS_FLOWING, LUX_IGNIS_BLOCK, LUX_IGNIS_BUCKET);
    }

    private static ForgeFlowingFluid.Properties unda(RegistryObject<FluidType> type, RegistryObject<ForgeFlowingFluid> source, RegistryObject<ForgeFlowingFluid> flowing, RegistryObject<LiquidBlock> block, RegistryObject<Item> bucket) {
        return new ForgeFlowingFluid.Properties(type, source, flowing).block(block).bucket(bucket).slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5);
    }

    private static ForgeFlowingFluid.Properties ignis(RegistryObject<FluidType> type, RegistryObject<ForgeFlowingFluid> source, RegistryObject<ForgeFlowingFluid> flowing, RegistryObject<LiquidBlock> block, RegistryObject<Item> bucket) {
        return new ForgeFlowingFluid.Properties(type, source, flowing).block(block).bucket(bucket).slopeFindDistance(2).levelDecreasePerBlock(2).tickRate(30);
    }

    private static RegistryObject<LiquidBlock> fluidBlock(String name, RegistryObject<ForgeFlowingFluid> source, MapColor color, int light) {
        return FLUID_BLOCKS.register(name, () -> {
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                    .mapColor(color)
                    .replaceable()
                    .noCollission()
                    .strength(100.0F)
                    .pushReaction(PushReaction.DESTROY)
                    .noLootTable()
                    .liquid()
                    .sound(SoundType.EMPTY);
            if (light > 0) {
                properties = properties.lightLevel(state -> light);
            }
            return new LiquidBlock(source, properties);
        });
    }

    private static RegistryObject<Item> bucket(String name, RegistryObject<ForgeFlowingFluid> source) {
        RegistryObject<Item> registered = BUCKETS.register(name, () -> new BucketItem(source.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
        EclipseGroup.addToMainTab(registered::get);
        return registered;
    }
}
