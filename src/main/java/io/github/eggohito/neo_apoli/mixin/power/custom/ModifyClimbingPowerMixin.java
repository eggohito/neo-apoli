package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.ModifyClimbingPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class ModifyClimbingPowerMixin extends Entity {

	@Shadow
	private Optional<BlockPos> lastClimbablePos;

	protected ModifyClimbingPowerMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Unique
	protected final ThreadLocal<WeakReference<Context>> neo_apoli$climbingContext = new ThreadLocal<>();

	@Unique
	protected Context neo_apoli$getOrCreateClimbingContext() {

		Context context = Optional.ofNullable(this.neo_apoli$climbingContext.get())
			.flatMap(reference -> Optional.ofNullable(reference.get()))
			.orElseGet(() -> ModifyClimbingPower.createContext(this));

		this.neo_apoli$climbingContext.set(new WeakReference<>(context));
		return context;

	}

	@ModifyReturnValue(method = "onClimbable", at = @At("RETURN"))
	private boolean modifyClimbing(boolean original) {

		if (original) {
			return true;
		}

		else if (this.isSpectator()) {
			return false;
		}

		else {

			Context context = this.neo_apoli$getOrCreateClimbingContext();
			boolean result = ModifyClimbingPower.modify(context, Power.Instance::isActive);

			if (result) {
				this.lastClimbablePos = Optional.of(this.blockPosition());
			}

			this.neo_apoli$climbingContext.remove();
			return result;

		}

	}

	@ModifyReturnValue(method = "isSuppressingSlidingDownLadder", at = @At("RETURN"))
	private boolean overrideClimbingHold(boolean original) {

		List<ModifyClimbingPower.Instance> instances = PowersComponent.getInstances(this, ModifyClimbingPower.Instance.class);
		boolean modified = original;

		if (!instances.isEmpty()) {

			Context context = this.neo_apoli$getOrCreateClimbingContext();
			modified = ModifyClimbingPower.modify(context, instances, ModifyClimbingPower.Instance::canHold);

			this.neo_apoli$climbingContext.remove();

		}

		return modified;

	}

}
