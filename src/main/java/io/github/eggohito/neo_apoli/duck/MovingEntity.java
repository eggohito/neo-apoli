package io.github.eggohito.neo_apoli.duck;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public interface MovingEntity {

	Vec3d neo_apoli$getVelocity();

	default void neo_apoli$setVelocity(Vec3d velocity) {

	}

	default void neo_apoli$setVelocity(double x, double y, double z) {
		this.neo_apoli$setVelocity(new Vec3d(x, y, z));
	}

	default double neo_apoli$getSquaredVelocityMagnitude() {
		Vec3d velocity = this.neo_apoli$getVelocity();
		return MathHelper.squaredMagnitude(velocity.getX(), velocity.getY(), velocity.getZ());
	}

	default double neo_apoli$getSquaredHorizontalVelocityMagnitude() {
		Vec3d velocity = this.neo_apoli$getVelocity();
		return velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ();
	}

	default double neo_apoli$getSquaredVerticalVelocityMagnitude() {
		Vec3d velocity = this.neo_apoli$getVelocity();
		return velocity.getY() * velocity.getY();
	}

}
