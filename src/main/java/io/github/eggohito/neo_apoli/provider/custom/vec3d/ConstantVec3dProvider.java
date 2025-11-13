package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public record ConstantVec3dProvider(double x, double y, double z) implements Vec3dProvider {

	public static final MapCodec<ConstantVec3dProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("x").forGetter(ConstantVec3dProvider::x),
		Codec.DOUBLE.fieldOf("y").forGetter(ConstantVec3dProvider::y),
		Codec.DOUBLE.fieldOf("z").forGetter(ConstantVec3dProvider::z)
	).apply(instance, ConstantVec3dProvider::new));

	public static final Codec<ConstantVec3dProvider> INLINE_CODEC = Vec3d.CODEC.xmap(
		ConstantVec3dProvider::new,
		ConstantVec3dProvider::get
	);

	public static final PacketCodec<RegistryByteBuf, ConstantVec3dProvider> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.DOUBLE, ConstantVec3dProvider::x,
		PacketCodecs.DOUBLE, ConstantVec3dProvider::y,
		PacketCodecs.DOUBLE, ConstantVec3dProvider::z,
		ConstantVec3dProvider::new
	);

	public ConstantVec3dProvider(Vec3d vec3d) {
		this(vec3d.getX(), vec3d.getY(), vec3d.getZ());
	}

	@Override
	public Vec3dProviderType<?> getType() {
		return Vec3dProviderTypes.CONSTANT;
	}

	@Override
	public @NotNull Vec3d next(Context context) {
		return get();
	}

	private Vec3d get() {
		return new Vec3d(x(), y(), z());
	}

}
