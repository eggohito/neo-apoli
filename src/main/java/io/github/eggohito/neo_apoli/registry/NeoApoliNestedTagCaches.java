package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.api.tag.NestedTagCache;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;

public final class NeoApoliNestedTagCaches {

	public static final NestedTagCache<EntityType<?>> ENTITY_TYPE = NestedTagCache.getOrCreate(Registries.ENTITY_TYPE);

	public static void registerAll() {

	}

}
