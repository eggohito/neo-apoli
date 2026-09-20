package io.github.eggohito.neo_apoli.provider.custom.number.floats;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.FloatProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliFloatProviderTypes;
import io.github.eggohito.neo_apoli.util.FloatConsumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record VectorLengthFloatProvider(Vec3Provider vector) implements FloatProvider {

	public static final MapCodec<VectorLengthFloatProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(Vec3Provider.CODEC.fieldOf("vector").forGetter(VectorLengthFloatProvider::vector))
		.apply(instance, VectorLengthFloatProvider::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, VectorLengthFloatProvider> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, VectorLengthFloatProvider::vector,
		VectorLengthFloatProvider::new
	);

	@Override
	public @NotNull FloatProvider.Type<?> getType() {
		return NeoApoliFloatProviderTypes.VECTOR_LENGTH;
	}

	@Override
	public void provideFloat(Context context, FloatConsumer setter) {
		vector()
			.getVec3(context.forChild(".vector"))
			.ifPresent(vector -> setter.accept((float) vector.length()));
	}

	@Override
	public void validate(Context.Validator validator) {
		FloatProvider.super.validate(validator);
		vector().validate(validator.forChild(".vector"));
	}

}
