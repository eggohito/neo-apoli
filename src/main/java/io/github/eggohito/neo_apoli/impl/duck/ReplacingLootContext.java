package io.github.eggohito.neo_apoli.impl.duck;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public interface ReplacingLootContext extends ContextKeySetHolder {

	default boolean neo_apoli$isReplaced(ResourceKey<LootTable> key) {
		throw new AssertionError("Implemented via mixin");
	}

	default void neo_apoli$setReplaced(ResourceKey<LootTable> key) {
		throw new AssertionError("Implemented via mixin");
	}

}
