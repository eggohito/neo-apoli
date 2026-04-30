package io.github.eggohito.neo_apoli.mixin.impl.power.custom.replace_loot_table;

import io.github.eggohito.neo_apoli.impl.misc.ContextKeySetHolder;
import io.github.eggohito.neo_apoli.impl.misc.ReplacingLootContext;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

@Mixin(LootContext.class)
public abstract class LootContextMixin implements ReplacingLootContext {

	@Shadow
	@Final
	private LootParams params;

	@Unique
	private final Set<ResourceKey<LootTable>> neo_apoli$replacedTables = new ObjectOpenHashSet<>();

	@Override
	public ContextKeySet neo_apoli$getKeySet() {
		return ((ContextKeySetHolder) this.params).neo_apoli$getKeySet();
	}

	@Override
	public void neo_apoli$setKeySet(ContextKeySet keySet) {

	}

	@Override
	public boolean neo_apoli$isReplaced(ResourceKey<LootTable> key) {
		return neo_apoli$replacedTables.contains(key);
	}

	@Override
	public void neo_apoli$setReplaced(ResourceKey<LootTable> key) {
		this.neo_apoli$replacedTables.add(key);
	}

}
