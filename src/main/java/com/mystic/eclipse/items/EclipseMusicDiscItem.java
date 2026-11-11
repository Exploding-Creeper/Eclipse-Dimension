package com.mystic.eclipse.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;

import java.util.function.Supplier;

public class EclipseMusicDiscItem extends RecordItem {

    public EclipseMusicDiscItem(int comparatorOutput, Supplier<SoundEvent> sound, Item.Properties settings, int lengthInSeconds) {
        super(comparatorOutput, sound, settings, lengthInSeconds);
    }
}
