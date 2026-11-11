package com.mystic.eclipse.worldgen;

import com.mystic.eclipse.utils.Reference;
import com.mystic.eclipse.worldgen.dimension.EclipseDimension;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public final class EclipseMobSpawns {
    private EclipseMobSpawns() {
    }

    @SubscribeEvent
    public static void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        if (!EclipseDimension.isEclipseDimension(event.getLevel().getLevel())) {
            return;
        }
        MobSpawnType type = event.getSpawnType();
        if (type == MobSpawnType.NATURAL
                || type == MobSpawnType.CHUNK_GENERATION
                || type == MobSpawnType.PATROL
                || type == MobSpawnType.EVENT
                || type == MobSpawnType.JOCKEY
                || type == MobSpawnType.STRUCTURE) {
            event.setSpawnCancelled(true);
        }
    }
}
