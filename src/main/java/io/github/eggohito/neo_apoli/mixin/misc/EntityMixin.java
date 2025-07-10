package io.github.eggohito.neo_apoli.mixin.misc;

import io.github.eggohito.neo_apoli.duck.MovingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements MovingEntity {

	@Shadow
	public abstract Vec3d getPos();

	@Shadow
	public abstract double getX();

	@Shadow
	public abstract double getY();

	@Shadow
	public abstract double getZ();

	@Unique
	private Vec3d neo_apoli$prevPos;

	@Unique
	private Vec3d neo_apoli$velocity;

	@Override
	public Vec3d neo_apoli$getVelocity() {
		return neo_apoli$velocity;
	}

	@Override
	public void neo_apoli$setVelocity(Vec3d velocity) {
		this.neo_apoli$velocity = velocity;
	}

	@Inject(method = "baseTick", at = @At("TAIL"))
	private void updateVelocity(CallbackInfo ci) {

		if (neo_apoli$prevPos == null) {
			this.neo_apoli$prevPos = this.getPos();
		}

		else {

			double dx = this.neo_apoli$prevPos.x - this.getX();
			double dy = this.neo_apoli$prevPos.y - this.getY();
			double dz = this.neo_apoli$prevPos.z - this.getZ();

			this.neo_apoli$setVelocity(dx, dy, dz);
			this.neo_apoli$prevPos = this.getPos();

		}

	}

}
