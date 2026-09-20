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

public record DistanceBetweenPositionsFloatProvider(Vec3Provider first, Vec3Provider second) implements FloatProvider {

	public static final MapCodec<DistanceBetweenPositionsFloatProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3Provider.CODEC.fieldOf("first").forGetter(DistanceBetweenPositionsFloatProvider::first),
		Vec3Provider.CODEC.fieldOf("second").forGetter(DistanceBetweenPositionsFloatProvider::second)
	).apply(instance, DistanceBetweenPositionsFloatProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DistanceBetweenPositionsFloatProvider> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, DistanceBetweenPositionsFloatProvider::first,
		Vec3Provider.STREAM_CODEC, DistanceBetweenPositionsFloatProvider::second,
		DistanceBetweenPositionsFloatProvider::new
	);

	@Override
	public @NotNull FloatProvider.Type<?> getType() {
		return NeoApoliFloatProviderTypes.DISTANCE_BETWEEN_POSITIONS;
	}

	@Override
	public void provideFloat(Context context, FloatConsumer setter) {
		first().getVec3(context.forChild(".first"))
			.ifPresent(first -> second().getVec3(context.forChild(".second"))
				.ifPresent(second -> setter.accept((float) first.distanceTo(second))));
	}

	@Override
	public void validate(Context.Validator validator) {
		FloatProvider.super.validate(validator);
		first().validate(validator.forChild(".first"));
		second().validate(validator.forChild(".second"));
	}

}
