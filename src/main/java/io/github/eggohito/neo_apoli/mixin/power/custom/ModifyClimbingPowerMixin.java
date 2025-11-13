package io.github.eggohito.neo_apoli.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyClimbingPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.event.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class ModifyClimbingPowerMixin extends Entity {

	@Shadow
	private Optional<BlockPos> climbingPos;

	protected ModifyClimbingPowerMixin(EntityType<?> type, World world) {
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

	@ModifyReturnValue(method = "isClimbing", at = @At("RETURN"))
	private boolean modifyClimbing(boolean original) {

		if (original) {
			return true;
		}

		else if (this.isSpectator()) {
			return false;
		}

		else {

			Context context = this.neo_apoli$getOrCreateClimbingContext();
			boolean result = PowersComponent.hasInstances(this, ModifyClimbingPower.Instance.class, instance -> instance.isActive(context));

			if (result) {
				this.climbingPos = Optional.of(this.getBlockPos());
			}

			this.neo_apoli$climbingContext.remove();
			return result;

		}

	}

	@ModifyReturnValue(method = "isHoldingOntoLadder", at = @At("RETURN"))
	private boolean overrideClimbingHold(boolean original) {

		List<ModifyClimbingPower.Instance> instances = PowersComponent.getInstances(this, ModifyClimbingPower.Instance.class);
		MutableBoolean modified = new MutableBoolean(false);

		if (instances.isEmpty()) {
			return original;
		}

		else {

			Context context = this.neo_apoli$getOrCreateClimbingContext();
			instances.forEach(instance -> modified.setValue(modified.getValue() || instance.canHold(context)));

			this.neo_apoli$climbingContext.remove();
			return modified.getValue();

		}

	}

}
