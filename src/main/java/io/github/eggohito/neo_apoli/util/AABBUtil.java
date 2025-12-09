package io.github.eggohito.neo_apoli.util;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AABBUtil {

	public static final AABB EMPTY = new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

	public static Direction getSideFromPoint(AABB aabb, Vec3 point) {

		Direction result = null;

		double cx = Mth.clamp(point.x(), aabb.minX, aabb.maxX);
		double cy = Mth.clamp(point.y(), aabb.minY, aabb.maxY);
		double cz = Mth.clamp(point.z(), aabb.minZ, aabb.maxZ);

		if (cx <= aabb.minX) {
			result = Direction.WEST;
		}

		if (cx >= aabb.maxX) {
			result = Direction.EAST;
		}

		if (cy <= aabb.minY) {
			result = Direction.DOWN;
		}

		if (cy >= aabb.maxY) {
			result = Direction.UP;
		}

		if (cz <= aabb.minZ) {
			result = Direction.NORTH;
		}

		if (cz >= aabb.maxZ) {
			result = Direction.SOUTH;
		}

		return result;

	}

}
