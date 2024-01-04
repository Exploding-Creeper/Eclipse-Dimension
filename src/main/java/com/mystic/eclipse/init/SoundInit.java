package com.mystic.eclipse.init;

import com.mystic.eclipse.utils.Reference;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundInit {

    //MUSIC DISC SOUNDS
    public static final SoundEvent WHITEWASH = register("music_disc.whitewash");
    public static final SoundEvent DAWNLIGHT = register("music_disc.dawnlight");
    public static final SoundEvent DOWNTIME = register("music_disc.downtime");

    private static SoundEvent register(String name) {
        Identifier id = new Identifier(Reference.MODID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}