package io.github.eggohito.neo_apoli.mixin.impl.power.custom.replace_loot_table;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.impl.misc.KeyableLootTable;
import io.github.eggohito.neo_apoli.impl.misc.ReplacingLootContext;
import io.github.eggohito.neo_apoli.power.custom.ReplaceLootTablePower;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.util.Reporter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.slf4j.event.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Mixin(LootTable.class)
public abstract class LootTableMixin implements KeyableLootTable {

	@Unique
	private ResourceKey<LootTable> neo_apoli$key;

	@Unique
	private ReloadableServerRegistries.Holder neo_apoli$holder;

	@Override
	public ResourceKey<LootTable> neo_apoli$getKey() {
		return this.neo_apoli$key;
	}

	@Override
	public void neo_apoli$setup(ResourceKey<LootTable> key, ReloadableServerRegistries.Holder holder) {
		this.neo_apoli$key = key;
		this.neo_apoli$holder = holder;
	}

	@Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At("HEAD"), cancellable = true)
	void replaceTable(LootContext lootContext, Consumer<ItemStack> output, CallbackInfo ci) {

		if (!(lootContext instanceof ReplacingLootContext replacingLootContext)) {
			return;
		}

		ContextKeySet keySet = replacingLootContext.neo_apoli$getKeySet();
		ResourceKey<LootTable> key = this.neo_apoli$getKey();

		if (key == null || replacingLootContext.neo_apoli$isReplaced(key)) {
			return;
		}

		Entity thisEntity = lootContext.getOptionalParameter(LootContextParams.THIS_ENTITY);
		Entity holder = thisEntity;

		if (keySet == LootContextParamSets.FISHING) {

			if (thisEntity instanceof FishingHook fishingHook) {
				holder = fishingHook.getOwner();
			}

		} else if (keySet == LootContextParamSets.ENTITY) {

			if (lootContext.hasParameter(LootContextParams.ATTACKING_ENTITY)) {
				holder = lootContext.getParameter(LootContextParams.ATTACKING_ENTITY);
			}

		} else if (keySet == LootContextParamSets.PIGLIN_BARTER) {

			if (thisEntity instanceof Piglin piglin) {
				holder = piglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).orElse(null);
			}

		}

		ReplaceLootTablePower.push((LootTable) (Object) this);

		Prioritized.InstanceCollection<ReplaceLootTablePower.Instance> instances = new Prioritized.InstanceCollection<>(holder, ReplaceLootTablePower.Instance.class);
		Optional<LootTable> replacementTable = Optional.empty();

		for (var instance : instances) {

			Context context = instance.createContext(holder, lootContext);
			Reporter reporter = context.reporter();

			if (instance.isActive(context)) {
				replacementTable = instance.getReplacement(context.forChild(".replacements"), key)
					.map(this.neo_apoli$holder::getLootTable)
					.filter(Predicate.not(LootTable.EMPTY::equals));
			}

			reporter.getErrorsFlattened().ifPresent(errors -> NeoApoli.logOnce(Level.WARN, "Found error(s) while trying to replace loot table \"" + key.location() + "\" " + errors));

		}

		if (replacementTable.isEmpty()) {
			return;
		}

		LootTable table = replacementTable.get();
		replacingLootContext.neo_apoli$setReplaced(key);

		table.getRandomItemsRaw(lootContext, output);
		ci.cancel();

	}

	@WrapMethod(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V")
	void wrapGetForReplacing(LootContext context, Consumer<ItemStack> output, Operation<Void> original) {

		try {
			original.call(context, output);
		}

		finally {
			ReplaceLootTablePower.clear();
		}

	}

	@Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootContext;pushVisitedElement(Lnet/minecraft/world/level/storage/loot/LootContext$VisitedEntry;)Z"))
	void popReplaced(LootContext context, Consumer<ItemStack> output, CallbackInfo ci) {
		ReplaceLootTablePower.pop();
	}

	@Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootContext;popVisitedElement(Lnet/minecraft/world/level/storage/loot/LootContext$VisitedEntry;)V"))
	void restoreReplaced(LootContext context, Consumer<ItemStack> output, CallbackInfo ci) {
		ReplaceLootTablePower.restore();
	}

}
