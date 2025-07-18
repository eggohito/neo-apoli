package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyInvisibilityPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

public abstract class ModifyInvisibilityPowerMixin {

	@Mixin(Entity.class)
	public static abstract class ProxyImpl {

		@ModifyReturnValue(method = "isInvisible", at = @At("RETURN"))
		private boolean invisibleProxy(boolean original) {
			return original
				|| PowersComponent.hasPowerImpl(thisAsEntity(), ModifyInvisibilityPower.Impl.class, ModifyInvisibilityPower.Impl::isActive);
		}

		@WrapOperation(method = "isInvisibleTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInvisible()Z"))
		private boolean invisibleToProxy(Entity entity, Operation<Boolean> original, PlayerEntity viewer) {

			if (viewer == null || !PowersComponent.hasPowerImpl(entity, ModifyInvisibilityPower.Impl.class)) {
				return original.call(entity);
			}

			else {
				return ModifyInvisibilityPower.isInvisibleTo(entity, viewer);
			}

		}

		@Unique
		private Entity thisAsEntity() {
			return (Entity) (Object) this;
		}

	}

	@Mixin(LivingEntity.class)
	public static abstract class ScalingProxyImpl {

		@WrapOperation(method = "getAttackDistanceScalingFactor", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvisible()Z"))
		private boolean invisibleToProxy(LivingEntity entity, Operation<Boolean> original, @Nullable Entity viewer) {

			if (viewer == null || !PowersComponent.hasPowerImpl(entity, ModifyInvisibilityPower.Impl.class)) {
				return original.call(entity);
			}

			else {
				return ModifyInvisibilityPower.isInvisibleTo(entity, viewer);
			}

		}

	}

}
