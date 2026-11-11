package com.mystic.eclipse.init;

import com.mystic.eclipse.creativetab.EclipseGroup;
import com.mystic.eclipse.items.EclipseMusicDiscItem;
import com.mystic.eclipse.utils.Reference;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MODID);

    private static final Item.Properties ECLIPSE_SETTINGS = new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON);

    public static final RegistryObject<Item> WHITEWASH_DISC = register("whitewash", () -> new EclipseMusicDiscItem(15, SoundInit.WHITEWASH, ECLIPSE_SETTINGS, 322));
    public static final RegistryObject<Item> DAWNLIGHT_DISC = register("dawnlight", () -> new EclipseMusicDiscItem(7, SoundInit.DAWNLIGHT, ECLIPSE_SETTINGS, 212));
    public static final RegistryObject<Item> DOWNTIME_DISC = register("downtime", () -> new EclipseMusicDiscItem(0, SoundInit.DOWNTIME, ECLIPSE_SETTINGS, 163));

    private static RegistryObject<Item> register(String name, java.util.function.Supplier<Item> item) {
        RegistryObject<Item> registered = ITEMS.register(name, item);
        EclipseGroup.addToMainTab(registered::get);
        return registered;
    }
}
