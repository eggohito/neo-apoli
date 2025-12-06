package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyAirSpeedPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

public abstract class ModifyAirSpeedPowerMixin {

	@Mixin(LivingEntity.class)
	public static abstract class LivingTarget extends Entity {

		protected LivingTarget(EntityType<?> entityType, Level level) {
			super(entityType, level);
		}

		@Unique
		protected final ThreadLocal<WeakReference<Context>> neo_apoli$airSpeedContext = new ThreadLocal<>();

		@Unique
		protected Context neo_apoli$getOrCreateAirSpeedContext() {

			Context context = Optional.ofNullable(this.neo_apoli$airSpeedContext.get())
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyAirSpeedPower.createContext(this));

			this.neo_apoli$airSpeedContext.set(new WeakReference<>(context));
			return context;

		}

		@ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
		private float modify(float original) {

			Context context = this.neo_apoli$getOrCreateAirSpeedContext();
			List<ModifyAirSpeedPower.Instance> instances = PowersComponent.getInstances(this, ModifyAirSpeedPower.Instance.class);

			this.neo_apoli$airSpeedContext.remove();
			return ModifyAirSpeedPower.modify(context, instances, original);

		}

	}

	@Mixin(net.minecraft.world.entity.player.Player.class)
	public static abstract class Player extends LivingEntity {

		protected Player(EntityType<? extends LivingEntity> entityType, Level level) {
			super(entityType, level);
		}

		@Unique
		protected final ThreadLocal<WeakReference<Context>> neo_apoli$airSpeedContext = new ThreadLocal<>();

		@Unique
		protected Context neo_apoli$getOrCreateAirSpeedContext() {

			Context context = Optional.ofNullable(this.neo_apoli$airSpeedContext.get())
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyAirSpeedPower.createContext(this));

			this.neo_apoli$airSpeedContext.set(new WeakReference<>(context));
			return context;

		}

		@ModifyReturnValue(method = "getFlyingSpeed", at = @At("RETURN"))
		private float modify(float original) {

			Context context = this.neo_apoli$getOrCreateAirSpeedContext();
			List<ModifyAirSpeedPower.Instance> instances = PowersComponent.getInstances(this, ModifyAirSpeedPower.Instance.class);

			this.neo_apoli$airSpeedContext.remove();
			return ModifyAirSpeedPower.modify(context, instances, original);

		}

	}

}
