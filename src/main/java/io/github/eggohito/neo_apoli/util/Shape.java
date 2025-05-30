package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public enum Shape implements StringIdentifiable {

	CUBE("cube", (x, y, z) -> Math.max(Math.max(x, y), z), (x, y, z) -> 0),
	CHEBYSHEV("chebyshev", CUBE.distanceGetter, CUBE.blockDistanceGetter),
	STAR("star", (x, y, z) -> x + y + z, (x, y, z) -> Math.abs(x) + Math.abs(y) + Math.abs(z)),
	MANHATTAN("manhattan", STAR.distanceGetter, STAR.blockDistanceGetter),
	SPHERE("sphere", (x, y, z) -> Math.sqrt(x * x + y * y + z * z), (x, y, z) -> Math.sqrt(x * x + y * y + z * z)),
	EUCLIDEAN("euclidean", SPHERE.distanceGetter, SPHERE.blockDistanceGetter);

	public static final Codec<Shape> CODEC = StringIdentifiable.createCodec(Shape::values);
	public static final PacketCodec<ByteBuf, Shape> PACKET_CODEC = PacketCodecs.indexed(ValueLists.createIndexToValueFunction(Shape::ordinal, Shape.values(), ValueLists.OutOfBoundsHandling.WRAP), Shape::ordinal);

	final String name;
	final DistanceGetter distanceGetter;
	final BlockDistanceGetter blockDistanceGetter;

	Shape(String name, DistanceGetter distanceGetter, BlockDistanceGetter blockDistanceGetter) {
		this.name = name;
		this.distanceGetter = distanceGetter;
		this.blockDistanceGetter = blockDistanceGetter;
	}

	@Override
	public String asString() {
		return name;
	}

	public List<BlockPos> getBlockPositions(BlockPos center, int radius) {

		List<BlockPos> collected = new ObjectArrayList<>();
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				for (int z = -radius; z <= radius; z++) {

					if (this.getBlockDistance(x, y, z) <= radius) {
						collected.add(new BlockPos(center.add(x, y, z)));
					}

				}
			}
		}

		return collected;

	}

	public List<Entity> getEntities(World world, Vec3d center, double radius) {

		List<Entity> collected = new ObjectArrayList<>();

		double diameter = radius * 2;
		double x, y, z;

		for (Entity entity : world.getNonSpectatingEntities(Entity.class, Box.of(center, diameter, diameter, diameter))) {

			x = Math.abs(entity.getX() - center.getX());
			y = Math.abs(entity.getY() - center.getY());
			z = Math.abs(entity.getZ() - center.getZ());

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
