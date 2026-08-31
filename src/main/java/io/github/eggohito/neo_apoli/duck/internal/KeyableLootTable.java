package io.github.eggohito.neo_apoli.duck.internal;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.storage.loot.LootTable;

public interface KeyableLootTable {

	default ResourceKey<LootTable> neo_apoli$getKey() {
		throw new AssertionError("Implemented via mixin");
	}

	default void neo_apoli$setup(ResourceKey<LootTable> key, ReloadableServerRegistries.Holder holder) {
		throw new AssertionError("Implemented via mixin");
	}

}
