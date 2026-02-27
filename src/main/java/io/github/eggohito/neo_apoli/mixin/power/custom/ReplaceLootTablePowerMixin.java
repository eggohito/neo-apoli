package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Either;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.impl.duck.ContextKeySetHolder;
import io.github.eggohito.neo_apoli.impl.duck.KeyableLootTable;
import io.github.eggohito.neo_apoli.impl.duck.ReplacingLootContext;
import io.github.eggohito.neo_apoli.power.custom.ReplaceLootTablePower;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.util.Reporter;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.slf4j.event.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class ReplaceLootTablePowerMixin {

	@Mixin(ReloadableServerRegistries.Holder.class)
	public static abstract class Replacer {

		@Inject(method = "<init>", at = @At("TAIL"))
		private void setupLootTables(HolderLookup.Provider registries, CallbackInfo ci) {
			registries.lookup(Registries.LOOT_TABLE).ifPresent(lootTables -> lootTables.listElements().forEach(reference -> {

				ResourceKey<LootTable> key = reference.key();

				if (reference.value() instanceof KeyableLootTable keyable) {
					keyable.neo_apoli$setup(key, (ReloadableServerRegistries.Holder) (Object) this);
				}

			}));
		}

		@ModifyReturnValue(method = "getLootTable", at = @At("RETURN"))
		private LootTable getReplacedTable(LootTable original, ResourceKey<LootTable> key) {

			if (key.equals(ReplaceLootTablePower.REPLACED_TABLE_KEY)) {
				return ReplaceLootTablePower.peek();
			}

			else {
				return original;
			}

		}

	}

	@Mixin(NestedLootTable.class)
	public static abstract class NestedReplacer {

		@SuppressWarnings("unchecked")
		@WrapOperation(method = "createItemStack", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Either;map(Ljava/util/function/Function;Ljava/util/function/Function;)Ljava/lang/Object;"))
		private <T, L extends ResourceKey<LootTable>, R extends LootTable> T replaceGetter(Either<L, R> either, Function<? super L, ? extends T> leftFunction, Function<? super R, ? extends T> rightFunction, Operation<T> original, Consumer<ItemStack> stackConsumer, LootContext lootContext) {

			ReloadableServerRegistries.Holder holder = lootContext.getLevel().getServer().reloadableRegistries();
			Function<? super L, ? extends T> newGetter = l -> (T) holder.getLootTable(l);

			return original.call(either, newGetter, rightFunction);

		}

	}

	@Mixin(LootTable.class)
	public static abstract class LootTableCache implements KeyableLootTable {

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
		private void replaceTable(LootContext lootContext, Consumer<ItemStack> output, CallbackInfo ci) {

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

			}

			else if (keySet == LootContextParamSets.ENTITY) {

				if (lootContext.hasParameter(LootContextParams.ATTACKING_ENTITY)) {
					holder = lootContext.getParameter(LootContextParams.ATTACKING_ENTITY);
				}

			}

			else if (keySet == LootContextParamSets.PIGLIN_BARTER) {

				if (thisEntity instanceof Piglin piglin) {
					holder = piglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).orElse(null);
				}

			}

			ReplaceLootTablePower.push((LootTable) (Object) this);

			Prioritized.InstanceCollection<ReplaceLootTablePower.Instance> instances = new Prioritized.InstanceCollection<>(holder, ReplaceLootTablePower.Instance.class);
			Optional<LootTable> replacementTable = Optional.empty();

			for (var instance : instances) {

				Context context = instance.createContext(lootContext);
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
		private void wrapGetForReplacing(LootContext context, Consumer<ItemStack> output, Operation<Void> original) {

			try {
				original.call(context, output);
			}

			finally {
				ReplaceLootTablePower.clear();
			}

		}

		@Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootContext;pushVisitedElement(Lnet/minecraft/world/level/storage/loot/LootContext$VisitedEntry;)Z"))
		private void popReplaced(LootContext context, Consumer<ItemStack> output, CallbackInfo ci) {
			ReplaceLootTablePower.pop();
		}

		@Inject(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootContext;popVisitedElement(Lnet/minecraft/world/level/storage/loot/LootContext$VisitedEntry;)V"))
		private void restoreReplaced(LootContext context, Consumer<ItemStack> output, CallbackInfo ci) {
			ReplaceLootTablePower.restore();
		}

	}

	@Mixin(LootContext.class)
	public static abstract class LootContextCache implements ReplacingLootContext {

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

	@Mixin(LootParams.class)
	public static abstract class LootParamsCache implements ContextKeySetHolder {

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

	@Mixin(LootParams.Builder.class)
	public static abstract class LootParamsCacheInit {

		@ModifyReturnValue(method = "create", at = @At("RETURN"))
		private LootParams cacheKeySet(LootParams original, ContextKeySet keySet) {

			((ContextKeySetHolder) original).neo_apoli$setKeySet(keySet);

			return original;

		}

	}

}
