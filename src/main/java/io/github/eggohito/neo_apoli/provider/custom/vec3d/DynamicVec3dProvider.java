package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record DynamicVec3dProvider(NumberProvider x, NumberProvider y, NumberProvider z) implements Vec3dProvider {

	public static final MapCodec<DynamicVec3dProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("x").forGetter(DynamicVec3dProvider::x),
		NumberProvider.CODEC.fieldOf("y").forGetter(DynamicVec3dProvider::y),
		NumberProvider.CODEC.fieldOf("z").forGetter(DynamicVec3dProvider::z)
	).apply(instance, DynamicVec3dProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicVec3dProvider> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, DynamicVec3dProvider::x,
		NumberProvider.STREAM_CODEC, DynamicVec3dProvider::y,
		NumberProvider.STREAM_CODEC, DynamicVec3dProvider::z,
		DynamicVec3dProvider::new
	);

	@Override
	public Vec3dProviderType<?> getType() {
		return Vec3dProviderTypes.DYNAMIC;
	}

	@Override
	public @NotNull Vec3 next(Context context) {
		return new Vec3(
			x().nextDouble(context.makeChild(".x")),
			y().nextDouble(context.makeChild(".y")),
			z().nextDouble(context.makeChild(".z"))
		);
	}

	@Override
	public void validate(ProblemReporter reporter) {

		Vec3dProvider.super.validate(reporter);

		x().validate(reporter.forChild(".x"));
		y().validate(reporter.forChild(".y"));
		z().validate(reporter.forChild(".z"));

	}

}
