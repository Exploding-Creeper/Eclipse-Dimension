package com.mystic.eclipse.init;

import com.mystic.eclipse.items.EclipseMusicDiscItem;
import com.mystic.eclipse.utils.Reference;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ItemInit {
    public static void init(){}

    public static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Reference.MODID, name), item);
    }

    private static final Item.Settings ECLIPSE_SETTINGS = new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).maxCount(1);

    //MUSIC DISC
    public static final Item WHITEWASH_DISC = register("whitewash", new EclipseMusicDiscItem(15, SoundInit.WHITEWASH, ECLIPSE_SETTINGS, 322));
    public static final Item DAWNLIGHT_DISC = register("dawnlight", new EclipseMusicDiscItem(7, SoundInit.DAWNLIGHT, ECLIPSE_SETTINGS, 212));
    public static final Item DOWNTIME_DISC = register("downtime", new EclipseMusicDiscItem(0, SoundInit.DOWNTIME, ECLIPSE_SETTINGS, 163));
}
