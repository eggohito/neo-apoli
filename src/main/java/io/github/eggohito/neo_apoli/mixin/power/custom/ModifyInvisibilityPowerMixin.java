package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.ModifyInvisibilityPower;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

public abstract class ModifyInvisibilityPowerMixin {

	@Mixin(Entity.class)
	public static abstract class ProxyImpl {

		@Shadow
		public abstract net.minecraft.world.level.Level level();

		@Shadow
		public abstract Vec3 position();

		@Unique
		private Entity neo_apoli$thisAsEntity() {
			return (Entity) (Object) this;
		}

		@ModifyReturnValue(method = "isInvisible", at = @At("RETURN"))
		private boolean invisibleProxy(boolean original) {

			try {
				return original
					|| ModifyInvisibilityPower.modify(neo_apoli$thisAsEntity(), null, Power.Instance::isActive, () -> false);
			}

			finally {
				ModifyInvisibilityPower.VISITOR.clear();
			}

		}

		@WrapOperation(method = "isInvisibleTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInvisible()Z"))
		private boolean invisibleToProxy(Entity entity, Operation<Boolean> original, Player viewer) {

			try {

				if (viewer == null) {
					return original.call(entity);
				}

				else {
					return ModifyInvisibilityPower.modify(neo_apoli$thisAsEntity(), viewer, ModifyInvisibilityPower.Instance::isInvisibleTo, () -> original.call(entity));
				}

			}

			finally {
				ModifyInvisibilityPower.VISITOR.clear();
			}

		}

	}

	@Mixin(LivingEntity.class)
	public static abstract class ScalingProxyImpl extends ProxyImpl {

		@WrapOperation(method = "getVisibilityPercent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInvisible()Z"))
		private boolean invisibleToProxy(LivingEntity entity, Operation<Boolean> original, @Nullable Entity viewer) {

			try {

				if (viewer == null) {
					return original.call(entity);
				}

				else {
					return ModifyInvisibilityPower.modify(entity, viewer, ModifyInvisibilityPower.Instance::isInvisibleTo, () -> original.call(entity));
				}

			}

			finally {
				ModifyInvisibilityPower.VISITOR.clear();
			}

		}

	}

}
