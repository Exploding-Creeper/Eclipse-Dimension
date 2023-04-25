package com.mystic.eclipse.creativetab;

import com.mystic.eclipse.EclipseMain;
import com.mystic.eclipse.init.BlockInit;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class EclipseGroup {
    public static void init(){
        FabricItemGroupBuilder.create(EclipseMain.id("general")).icon(() -> BlockInit.TWILIGHT_STONE_BLOCK.asItem().getDefaultStack()).appendItems((stacks) -> {
            Registry.ITEM.stream().filter((item) -> Registry.ITEM.getId(item).getNamespace().equals("eclipse")).forEach((item) -> stacks.add(new ItemStack(item)));
        }).build();
    }
}
