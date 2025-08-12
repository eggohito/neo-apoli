package io.github.eggohito.neo_apoli.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.Vec3d;

public class NeoApoliMapCodecs {

	public static final MapCodec<Vec3d> VECTOR_3_DOUBLE = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("x").forGetter(Vec3d::getX),
		Codec.DOUBLE.fieldOf("y").forGetter(Vec3d::getY),
		Codec.DOUBLE.fieldOf("z").forGetter(Vec3d::getZ)
	).apply(instance, Vec3d::new));

}
