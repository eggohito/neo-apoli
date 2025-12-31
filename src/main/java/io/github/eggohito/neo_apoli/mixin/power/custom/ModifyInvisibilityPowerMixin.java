package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.ModifyInvisibilityPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

public abstract class ModifyInvisibilityPowerMixin {

	@Mixin(Entity.class)
	public static abstract class ProxyImpl {

		@Shadow
		public abstract net.minecraft.world.level.Level level();

		@Shadow
		public abstract Vec3 position();

		@Unique
		protected final ThreadLocal<WeakReference<Context>> neo_apoli$invisibilityContext = new ThreadLocal<>();

		@Unique
		protected Context neo_apoli$getOrCreateInvisibilityContext(@Nullable Entity viewer) {

			Context context = Optional.ofNullable(this.neo_apoli$invisibilityContext.get())
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyInvisibilityPower.createContext((Entity) (Object) this, viewer));

			this.neo_apoli$invisibilityContext.set(new WeakReference<>(context));
			return context;

		}

		@Unique
		private Entity neo_apoli$thisAsEntity() {
			return (Entity) (Object) this;
		}

		@ModifyReturnValue(method = "isInvisible", at = @At("RETURN"))
		private boolean invisibleProxy(boolean original) {

			if (original) {
				return true;
			}

			List<ModifyInvisibilityPower.Instance> instances = PowersComponent.getInstances(this.neo_apoli$thisAsEntity(), ModifyInvisibilityPower.Instance.class);
			Context context = this.neo_apoli$getOrCreateInvisibilityContext(null);

			boolean result = ModifyInvisibilityPower.doesApply(context, instances, Power.Instance::isActive, () -> false);
			this.neo_apoli$invisibilityContext.remove();

			return result;

		}

		@WrapOperation(method = "isInvisibleTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInvisible()Z"))
		private boolean invisibleToProxy(Entity entity, Operation<Boolean> original, Player viewer) {

			if (viewer == null) {
				return original.call(entity);
			}

			List<ModifyInvisibilityPower.Instance> instances = PowersComponent.getInstances(entity, ModifyInvisibilityPower.Instance.class);
			Context context = this.neo_apoli$getOrCreateInvisibilityContext(viewer);

			boolean result = ModifyInvisibilityPower.doesApply(context, instances, ModifyInvisibilityPower.Instance::isInvisibleTo, () -> original.call(entity));
			this.neo_apoli$invisibilityContext.remove();

			return result;

		}

	}

	@Mixin(LivingEntity.class)
	public static abstract class ScalingProxyImpl extends ProxyImpl {

		@WrapOperation(method = "getVisibilityPercent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInvisible()Z"))
		private boolean invisibleToProxy(LivingEntity entity, Operation<Boolean> original, @Nullable Entity viewer) {

			if (viewer == null) {
				return original.call(entity);
			}

			List<ModifyInvisibilityPower.Instance> instances = PowersComponent.getInstances(entity, ModifyInvisibilityPower.Instance.class);
			Context context = this.neo_apoli$getOrCreateInvisibilityContext(viewer);

			boolean result = ModifyInvisibilityPower.doesApply(context, instances, ModifyInvisibilityPower.Instance::isInvisibleTo, () -> original.call(entity));
			this.neo_apoli$invisibilityContext.remove();

			return result;

		}

	}

}
