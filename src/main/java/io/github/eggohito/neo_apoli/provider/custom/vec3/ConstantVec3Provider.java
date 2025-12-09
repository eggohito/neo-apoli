package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ConstantVec3Provider(double x, double y, double z) implements Vec3Provider {

	public static final MapCodec<ConstantVec3Provider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("x").forGetter(ConstantVec3Provider::x),
		Codec.DOUBLE.fieldOf("y").forGetter(ConstantVec3Provider::y),
		Codec.DOUBLE.fieldOf("z").forGetter(ConstantVec3Provider::z)
	).apply(instance, ConstantVec3Provider::new));

	public static final Codec<ConstantVec3Provider> INLINE_CODEC = Vec3.CODEC.xmap(
		ConstantVec3Provider::new,
		ConstantVec3Provider::get
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantVec3Provider> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, ConstantVec3Provider::x,
		ByteBufCodecs.DOUBLE, ConstantVec3Provider::y,
		ByteBufCodecs.DOUBLE, ConstantVec3Provider::z,
		ConstantVec3Provider::new
	);

	public ConstantVec3Provider(Vec3 vec3d) {
		this(vec3d.x(), vec3d.y(), vec3d.z());
	}

	@Override
	public Vec3ProviderType<?> getType() {
		return Vec3ProviderTypes.CONSTANT;
	}

	@Override
	public @NotNull Vec3 next(Context context) {
		return get();
	}

	private Vec3 get() {
		return new Vec3(x(), y(), z());
	}

}
