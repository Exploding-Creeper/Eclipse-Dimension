package com.mystic.eclipse.sounds;

import com.mystic.eclipse.utils.Reference;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EclipseSoundEvents {

    //MUSIC DISC SOUNDS
    public static final SoundEvent WHITEWASH = register("whitewash");
    public static final SoundEvent DAWNLIGHT = register("dawnlight");

    private static SoundEvent register(String name) {
        Identifier id = new Identifier(Reference.MODID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }
}