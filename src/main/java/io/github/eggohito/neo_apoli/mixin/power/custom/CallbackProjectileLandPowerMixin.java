package io.github.eggohito.neo_apoli.mixin.power.custom;

import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.CallbackProjectileLandPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public abstract class CallbackProjectileLandPowerMixin extends Entity implements TraceableEntity {

	@Nullable
	@Shadow
	public abstract Entity getOwner();

	private CallbackProjectileLandPowerMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "onHit", at = @At("TAIL"))
	private void executeActions(HitResult hitResult, CallbackInfo ci) {

		Entity owner = this.getOwner();
		HitResult.Type hitType = hitResult.getType();

		if (owner == null || hitType == HitResult.Type.MISS) {
			return;
		}

		Context context = CallbackProjectileLandPower.createContext(owner, (Projectile) (Object) this, hitResult);
		CallbackProjectileLandPower.execute(context, PowersComponent.getInstances(owner, CallbackProjectileLandPower.Instance.class));

	}

}
