package io.github.eggohito.neo_apoli.provider;

import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import net.minecraft.loot.context.LootContextAware;

public interface ValueProvider extends LootContextAware {

	ValueProviderType<?> getType();

}
