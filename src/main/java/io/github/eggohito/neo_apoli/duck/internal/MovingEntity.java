package io.github.eggohito.neo_apoli.duck.internal;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public interface MovingEntity {

	Vec3 neo_apoli$getVelocity();

	default void neo_apoli$setVelocity(Vec3 velocity) {

	}

	default void neo_apoli$setVelocity(double x, double y, double z) {
		this.neo_apoli$setVelocity(new Vec3(x, y, z));
	}

	default double neo_apoli$getSquaredVelocityMagnitude() {
		Vec3 velocity = this.neo_apoli$getVelocity();
		return Mth.lengthSquared(velocity.x(), velocity.y(), velocity.z());
	}

	default double neo_apoli$getSquaredHorizontalVelocityMagnitude() {
		Vec3 velocity = this.neo_apoli$getVelocity();
		return velocity.x() * velocity.x() + velocity.z() * velocity.z();
	}

	default double neo_apoli$getSquaredVerticalVelocityMagnitude() {
		Vec3 velocity = this.neo_apoli$getVelocity();
		return velocity.y() * velocity.y();
	}

}
