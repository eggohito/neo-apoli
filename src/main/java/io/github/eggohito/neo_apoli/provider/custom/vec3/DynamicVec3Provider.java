package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliVec3ProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record DynamicVec3Provider(NumberProvider x, NumberProvider y, NumberProvider z) implements Vec3Provider {

	public static final MapCodec<DynamicVec3Provider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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
	public @NotNull Vec3Provider.Type<?> getType() {
		return NeoApoliVec3ProviderTypes.DYNAMIC;
	}

	@Override
	public @NotNull Vec3 nextVec3(Context context) {
		return new Vec3(
			x().nextDouble(context.forChild(".x")),
			y().nextDouble(context.forChild(".y")),
			z().nextDouble(context.forChild(".z"))
		);
	}

	@Override
	public void validate(Context.Validator validator) {

		Vec3Provider.super.validate(validator);

		x().validate(validator.forChild(".x"));
		y().validate(validator.forChild(".y"));
		z().validate(validator.forChild(".z"));

	}

}
