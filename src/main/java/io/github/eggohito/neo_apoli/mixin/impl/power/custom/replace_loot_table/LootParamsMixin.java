package io.github.eggohito.neo_apoli.mixin.impl.power.custom.replace_loot_table;

import io.github.eggohito.neo_apoli.impl.misc.ContextKeySetHolder;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

@Mixin(LootParams.class)
public abstract class LootParamsMixin implements ContextKeySetHolder {

	@Unique
	private ContextKeySet neo_apoli$keySet;

	@Override
	public ContextKeySet neo_apoli$getKeySet() {
		return Objects.requireNonNull(this.neo_apoli$keySet, "Loot params not initialized properly!");
	}

	@Override
	public void neo_apoli$setKeySet(ContextKeySet keySet) {
		this.neo_apoli$keySet = keySet;
	}

}
