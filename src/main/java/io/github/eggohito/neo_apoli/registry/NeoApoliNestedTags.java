package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.tag.NestedTag;
import io.github.eggohito.neo_apoli.tag.manager.NestedTagManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;

public final class NeoApoliNestedTags {

	public static final NestedTag<EntityType<?>> ENTITY_TYPE = NestedTagManager.INSTANCE.getOrCreate(Registries.ENTITY_TYPE);

	public static void registerAll() {

	}

}
