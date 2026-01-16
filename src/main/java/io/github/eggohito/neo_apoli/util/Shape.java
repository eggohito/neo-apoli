package io.github.eggohito.neo_apoli.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public enum Shape implements DistanceGetter {

	CUBE {

		@Override
		public double getDistance(double x, double y, double z) {
			return Math.max(Math.max(x, y), z);
		}

	},

	STAR {

		@Override
		public double getDistance(double x, double y, double z) {
			return x + y + z;
		}

	},

	SPHERE {

		@Override
		public double getDistance(double x, double y, double z) {
			return Math.sqrt(x * x + y * y + z * z);
		}

	};

	private static final ImmutableMap<String, Shape> ALIASES = ImmutableMap.<String, Shape>builder()
		.put("chebyshev", CUBE)
		.put("manhattan", STAR)
		.put("euclidean", SPHERE)
		.build();

	public static final Codec<Shape> CODEC = CodecUtil.enumType(Shape.class, ALIASES);
	public static final StreamCodec<ByteBuf, Shape> STREAM_CODEC = StreamCodecUtil.enumType(Shape.class);

	public List<BlockPos> getBlockPositions(BlockPos center, int radius) {

		List<BlockPos> collected = new ObjectArrayList<>();
		int x, y, z;

		for (x = -radius; x <= radius; x++) {
			for (y = -radius; y <= radius; y++) {
				for (z = -radius; z <= radius; z++) {

					BlockPos pos = center.offset(x, y, z);

					if (this.getDistance(x, y, z) <= radius) {
						collected.add(pos);
					}

				}
			}
		}

		return collected;

	}

	public List<Entity> getEntities(Level level, Vec3 center, double radius) {

		List<Entity> collected = new ObjectArrayList<>();

		double diameter = radius * 2;
		double x, y, z;

		for (Entity entity : level.getEntitiesOfClass(Entity.class, AABB.ofSize(center, diameter, diameter, diameter))) {

			x = center.x() - entity.getX();
			y = center.y() - entity.getY();
			z = center.z() - entity.getZ();

			if (this.getDistance(x, y, z) <= radius + 1) {
				collected.add(entity);
			}

		}

		return collected;

	}

}
