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

public enum Shape  {

	CUBE((x, y, z) -> Math.max(Math.max(x, y), z), (x, y, z) -> 0),
	STAR((x, y, z) -> x + y + z, (x, y, z) -> Math.abs(x) + Math.abs(y) + Math.abs(z)),
	SPHERE((x, y, z) -> Math.sqrt(x * x + y * y + z * z), (x, y, z) -> Math.sqrt(x * x + y * y + z * z));

	private static final ImmutableMap<String, Shape> ALIASES = ImmutableMap.<String, Shape>builder()
		.put("chebyshev", CUBE)
		.put("manhattan", STAR)
		.put("euclidean", SPHERE)
		.build();

	public static final Codec<Shape> CODEC = CodecUtil.enumType(Shape.class, ALIASES);
	public static final StreamCodec<ByteBuf, Shape> STREAM_CODEC = StreamCodecUtil.enumType(Shape.class);

	final DistanceGetter distanceGetter;
	final BlockDistanceGetter blockDistanceGetter;

	Shape(DistanceGetter distanceGetter, BlockDistanceGetter blockDistanceGetter) {
		this.distanceGetter = distanceGetter;
		this.blockDistanceGetter = blockDistanceGetter;
	}

	public List<BlockPos> getBlockPositions(BlockPos center, int radius) {

		List<BlockPos> collected = new ObjectArrayList<>();
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {

					if (this.getBlockDistance(x, y, z) <= radius) {
						collected.add(new BlockPos(center.offset(x, y, z)));
					}

				}
			}
		}

		return collected;

	}

	public List<Entity> getEntities(Level world, Vec3 center, double radius) {

		List<Entity> collected = new ObjectArrayList<>();

		double diameter = radius * 2;
		double x, y, z;

		for (Entity entity : world.getEntitiesOfClass(Entity.class, AABB.ofSize(center, diameter, diameter, diameter))) {

			x = Math.abs(entity.getX() - center.x());
			y = Math.abs(entity.getY() - center.y());
			z = Math.abs(entity.getZ() - center.z());

			if (this.getDistance(x, y, z) <= radius + 1) {
				collected.add(entity);
			}

		}

		return collected;

	}

	public double getBlockDistance(int x, int y, int z) {
		return blockDistanceGetter.get(x, y, z);
	}

	public double getDistance(double x, double y, double z) {
		return distanceGetter.get(x, y, z);
	}

	@FunctionalInterface
	public interface BlockDistanceGetter {
		double get(int x, int y, int z);
	}

	@FunctionalInterface
	public interface DistanceGetter {
		double get(double x, double y, double z);
	}

}
