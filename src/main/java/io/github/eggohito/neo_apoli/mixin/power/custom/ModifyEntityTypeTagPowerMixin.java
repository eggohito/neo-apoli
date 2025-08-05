package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.DependencyTracker;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ModifyEntityTypeTagPowerMixin {

	@Mixin(TagGroupLoader.class)
	public static abstract class TagCache<T> {

		@Shadow
		@Final
		private String dataType;

		@Inject(method = "buildGroup", at = @At("RETURN"))
		private void cacheEntityTypeTags(Map<Identifier, List<TagGroupLoader.TrackedEntry>> tags, CallbackInfoReturnable<Map<Identifier, List<T>>> cir, @Local TagEntry.ValueGetter<T> getter, @Local DependencyTracker<Identifier, TagGroupLoader.TagDependencies> dependencyTracker) {
			ModifyEntityTypeTagPower.setCache(this.dataType, getter, dependencyTracker);
		}

	}

	@Mixin(Entity.class)
	public static abstract class EntityCache {

		@Shadow
		public abstract EntityType<?> getType();

		@ModifyReturnValue(method = "getType", at = @At("RETURN"))
		private EntityType<?> cacheEntityToType(EntityType<?> original) {

			if (original instanceof io.github.eggohito.neo_apoli.duck.EntityCache entityCache) {
				entityCache.neo_apoli$setEntity((Entity) (Object) this);
			}

			return original;

		}

		@ModifyExpressionValue(method = "handleFallDamage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;type:Lnet/minecraft/entity/EntityType;"))
		private EntityType<?> fixTypeCalls(EntityType<?> original) {
			return this.getType();
		}

	}

	@Mixin(EntityType.class)
	public static abstract class IsInTagProxy implements io.github.eggohito.neo_apoli.duck.EntityCache {

		@Unique
		protected final ThreadLocal<WeakReference<Context>> neo_apoli$modifiedTagContext = new ThreadLocal<>();

		@SuppressWarnings("ConstantValue")
		@Unique
		protected Optional<Context> neo_apoli$getOrCreateModifiedTagContext() {

			Entity entity = this.neo_apoli$getEntity();
			WeakReference<Context> contextReference = this.neo_apoli$modifiedTagContext.get();

			if (entity == null || entity.asComponentProvider().getComponentContainer() == null) {
				return Optional.empty();
			}

			else {

				if (contextReference == null || contextReference.get() == null) {
					contextReference = new WeakReference<>(ModifyEntityTypeTagPower.createContext(entity));
				}

				return Optional.ofNullable(contextReference.get());

			}

		}

		@Unique
		protected final ThreadLocal<WeakReference<Entity>> neo_apoli$currentEntity = new ThreadLocal<>();

		@Override
		public @Nullable Entity neo_apoli$getEntity() {
			return Optional.ofNullable(neo_apoli$currentEntity.get())
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElse(null);
		}

		@Override
		public void neo_apoli$setEntity(Entity entity) {

			if (entity == null) {
				this.neo_apoli$currentEntity.remove();
			}

			else {
				this.neo_apoli$currentEntity.set(new WeakReference<>(entity));
			}

		}

		@ModifyReturnValue(method = "isIn(Lnet/minecraft/registry/tag/TagKey;)Z", at = @At("RETURN"))
		private boolean normalProxy(boolean original, TagKey<EntityType<?>> tag) {

			if (original) {
				return true;
			}

			else {

				Optional<Context> optContext = this.neo_apoli$getOrCreateModifiedTagContext();
				boolean result = optContext
					.map(context -> ModifyEntityTypeTagPower.doesApply(context, tag))
					.orElse(false);

				this.neo_apoli$modifiedTagContext.remove();
				return result;

			}

		}

		@ModifyReturnValue(method = "isIn(Lnet/minecraft/registry/entry/RegistryEntryList;)Z", at = @At("RETURN"))
		private boolean entryListProxy(boolean original, RegistryEntryList<EntityType<?>> tagsEntryList) {

			if (original) {
				return true;
			}

			else {

				Optional<Context> optContext = this.neo_apoli$getOrCreateModifiedTagContext();
				boolean result = optContext
					.map(context -> ModifyEntityTypeTagPower.doesApply(context, tagsEntryList))
					.orElse(false);

				this.neo_apoli$modifiedTagContext.remove();
				return result;

			}

		}

	}

}
