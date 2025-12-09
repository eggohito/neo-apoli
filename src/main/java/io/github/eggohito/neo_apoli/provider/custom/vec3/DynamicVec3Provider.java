package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record DynamicVec3Provider(NumberProvider x, NumberProvider y, NumberProvider z) implements Vec3Provider {

	public static final MapCodec<DynamicVec3Provider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberProvider.CODEC.fieldOf("x").forGetter(DynamicVec3Provider::x),
		NumberProvider.CODEC.fieldOf("y").forGetter(DynamicVec3Provider::y),
		NumberProvider.CODEC.fieldOf("z").forGetter(DynamicVec3Provider::z)
	).apply(instance, DynamicVec3Provider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicVec3Provider> STREAM_CODEC = StreamCodec.composite(
		NumberProvider.STREAM_CODEC, DynamicVec3Provider::x,
		NumberProvider.STREAM_CODEC, DynamicVec3Provider::y,
		NumberProvider.STREAM_CODEC, DynamicVec3Provider::z,
		DynamicVec3Provider::new
	);

	@Override
	public Vec3ProviderType<?> getType() {
		return Vec3ProviderTypes.DYNAMIC;
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

		Vec3Provider.super.validate(reporter);

		x().validate(reporter.forChild(".x"));
		y().validate(reporter.forChild(".y"));
		z().validate(reporter.forChild(".z"));

	}

}
