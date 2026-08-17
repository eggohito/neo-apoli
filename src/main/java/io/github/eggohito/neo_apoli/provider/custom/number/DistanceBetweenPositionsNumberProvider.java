package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record DistanceBetweenPositionsNumberProvider(Vec3Provider first, Vec3Provider second) implements NumberProvider {

	public static final MapCodec<DistanceBetweenPositionsNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3Provider.CODEC.fieldOf("first").forGetter(DistanceBetweenPositionsNumberProvider::first),
		Vec3Provider.CODEC.fieldOf("second").forGetter(DistanceBetweenPositionsNumberProvider::second)
	).apply(instance, DistanceBetweenPositionsNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, DistanceBetweenPositionsNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Vec3Provider.STREAM_CODEC, DistanceBetweenPositionsNumberProvider::first,
		Vec3Provider.STREAM_CODEC, DistanceBetweenPositionsNumberProvider::second,
		DistanceBetweenPositionsNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.DISTANCE_BETWEEN_POSITIONS;
	}

	@Override
	public double getDouble(Context context) {
		return first().getVec3(context.forChild(".first"))
			.flatMap(first -> second().getVec3(context.forChild(".second"))
				.map(first::distanceTo)).orElse(0.0D);
	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		first().validate(validator.forChild(".first"));
		second().validate(validator.forChild(".second"));

	}

}
