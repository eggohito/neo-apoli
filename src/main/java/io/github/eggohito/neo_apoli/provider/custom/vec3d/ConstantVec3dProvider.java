package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ConstantVec3dProvider(double x, double y, double z) implements Vec3dProvider {

	public static final MapCodec<ConstantVec3dProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("x").forGetter(ConstantVec3dProvider::x),
		Codec.DOUBLE.fieldOf("y").forGetter(ConstantVec3dProvider::y),
		Codec.DOUBLE.fieldOf("z").forGetter(ConstantVec3dProvider::z)
	).apply(instance, ConstantVec3dProvider::new));

	public static final Codec<ConstantVec3dProvider> INLINE_CODEC = Vec3.CODEC.xmap(
		ConstantVec3dProvider::new,
		ConstantVec3dProvider::get
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantVec3dProvider> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, ConstantVec3dProvider::x,
		ByteBufCodecs.DOUBLE, ConstantVec3dProvider::y,
		ByteBufCodecs.DOUBLE, ConstantVec3dProvider::z,
		ConstantVec3dProvider::new
	);

	public ConstantVec3dProvider(Vec3 vec3d) {
		this(vec3d.x(), vec3d.y(), vec3d.z());
	}

	@Override
	public Vec3dProviderType<?> getType() {
		return Vec3dProviderTypes.CONSTANT;
	}

	@Override
	public @NotNull Vec3 next(Context context) {
		return get();
	}

	private Vec3 get() {
		return new Vec3(x(), y(), z());
	}

}
