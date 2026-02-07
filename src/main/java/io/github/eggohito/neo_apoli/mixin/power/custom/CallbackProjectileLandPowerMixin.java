package io.github.eggohito.neo_apoli.mixin.power.custom;

import io.github.eggohito.neo_apoli.power.custom.CallbackProjectileLandPower;
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
	private void executeActions(HitResult result, CallbackInfo ci) {

		Entity owner = this.getOwner();
		Projectile thisAsProjectile = (Projectile) (Object) this;

		if (result.getType() == HitResult.Type.MISS) {
			return;
		}

		CallbackProjectileLandPower.executeAsOwner(owner, thisAsProjectile, result);
		CallbackProjectileLandPower.executeAsProjectile(owner, thisAsProjectile, result);

	}

}
