package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.power.custom.ModifyEntityTypeTagPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;

public abstract class ModifyEntityTypeTagPowerMixin {

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

		@ModifyExpressionValue(method = "causeFallDamage", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;type:Lnet/minecraft/world/entity/EntityType;", opcode = Opcodes.GETFIELD))
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

		@ModifyReturnValue(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("RETURN"))
		private boolean normalProxy(boolean original, TagKey<EntityType<?>> tag) {

			if (original) {
				return true;
			}

			else {

				boolean result = this.neo_apoli$getOrCreateModifiedTagContext()
					.map(context -> ModifyEntityTypeTagPower.doesApply(context, tag))
					.orElse(false);

				this.neo_apoli$modifiedTagContext.remove();
				return result;

			}

		}

		@ModifyReturnValue(method = "is(Lnet/minecraft/core/HolderSet;)Z", at = @At("RETURN"))
		private boolean entryListProxy(boolean original, HolderSet<EntityType<?>> tagsEntryList) {

			if (original) {
				return true;
			}

			else {

				boolean result = this.neo_apoli$getOrCreateModifiedTagContext()
					.map(context -> ModifyEntityTypeTagPower.doesApply(context, tagsEntryList))
					.orElse(false);

				this.neo_apoli$modifiedTagContext.remove();
				return result;

			}

		}

	}

}
