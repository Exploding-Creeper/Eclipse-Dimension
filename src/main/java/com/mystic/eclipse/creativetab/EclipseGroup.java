package com.mystic.eclipse.creativetab;

import com.mystic.eclipse.init.BlockInit;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroupBuilderImpl;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EclipseGroup {
    public static final List<Supplier<? extends ItemConvertible>> MAIN_BLOCKS = new ArrayList<>();
    public static final ItemGroup owo = new FabricItemGroupBuilderImpl().icon(() -> BlockInit.TWILIGHT_STONE_BLOCK.asItem().getDefaultStack()).displayName(Text.literal("Eclipse")).entries((displayContext, entries) -> {
        MAIN_BLOCKS.forEach((itemLike -> entries.add(itemLike.get())));
    }).build();

    public static ItemConvertible addToMainTab (ItemConvertible itemLike) {
        MAIN_BLOCKS.add(() -> itemLike);
        return itemLike;
    }
}
