package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
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
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DISTANCE_BETWEEN_POSITIONS;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context firstContext = context.forChild(".first");
		Vec3 first = first().next(firstContext);

		if (firstContext.hasErrors()) {
			return 0.0d;
		}

		Context secondContext = context.forChild(".second");
		Vec3 second = second().next(secondContext);

		if (secondContext.hasErrors()) {
			return 0.0d;
		}

		return first.distanceTo(second);

	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		first().validate(validator.forChild(".first"));
		second().validate(validator.forChild(".second"));

	}

}
