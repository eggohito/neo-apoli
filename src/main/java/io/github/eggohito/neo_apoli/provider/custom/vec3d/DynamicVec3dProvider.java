package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public record DynamicVec3dProvider(NumberProvider x, NumberProvider y, NumberProvider z) implements Vec3dProvider {

	public static final MapCodec<DynamicVec3dProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("x").forGetter(DynamicVec3dProvider::x),
		NumberProvider.CODEC.fieldOf("y").forGetter(DynamicVec3dProvider::y),
		NumberProvider.CODEC.fieldOf("z").forGetter(DynamicVec3dProvider::z)
	).apply(instance, DynamicVec3dProvider::new));

	public static final PacketCodec<RegistryByteBuf, DynamicVec3dProvider> PACKET_CODEC = PacketCodec.tuple(
		NumberProvider.PACKET_CODEC, DynamicVec3dProvider::x,
		NumberProvider.PACKET_CODEC, DynamicVec3dProvider::y,
		NumberProvider.PACKET_CODEC, DynamicVec3dProvider::z,
		DynamicVec3dProvider::new
	);

	@Override
	public Vec3dProviderType<?> getType() {
		return Vec3dProviderTypes.DYNAMIC;
	}

	@Override
	public @NotNull Vec3d next(Context context) {
		return new Vec3d(
			x().nextDouble(context.makeChild(".x")),
			y().nextDouble(context.makeChild(".y")),
			z().nextDouble(context.makeChild(".z"))
		);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		Vec3dProvider.super.validate(reporter);

		x().validate(reporter.makeChild(".x"));
		y().validate(reporter.makeChild(".y"));
		z().validate(reporter.makeChild(".z"));

	}

}
