package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AABB.class)
public interface AABBAccessor {

	@Nullable
	@Invoker
	static Direction callGetDirection(AABB aabb, Vec3 start, double[] minDistance, @Nullable Direction facing, double deltaX, double deltaY, double deltaZ) {
		throw new AssertionError();
	}

}
