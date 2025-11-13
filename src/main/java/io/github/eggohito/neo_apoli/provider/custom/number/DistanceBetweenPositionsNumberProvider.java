package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public record DistanceBetweenPositionsNumberProvider(Vec3dProvider first, Vec3dProvider second) implements NumberProvider {

	public static final MapCodec<DistanceBetweenPositionsNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3dProvider.CODEC.fieldOf("first").forGetter(DistanceBetweenPositionsNumberProvider::first),
		Vec3dProvider.CODEC.fieldOf("second").forGetter(DistanceBetweenPositionsNumberProvider::second)
	).apply(instance, DistanceBetweenPositionsNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, DistanceBetweenPositionsNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		Vec3dProvider.PACKET_CODEC, DistanceBetweenPositionsNumberProvider::first,
		Vec3dProvider.PACKET_CODEC, DistanceBetweenPositionsNumberProvider::second,
		DistanceBetweenPositionsNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DISTANCE_BETWEEN_POSITIONS;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context firstContext = context.makeChild(".first");
		Vec3d first = first().next(firstContext);

		if (firstContext.hasErrors()) {
			return 0.0d;
		}

		Context secondContext = context.makeChild(".second");
		Vec3d second = second().next(secondContext);

		if (secondContext.hasErrors()) {
			return 0.0d;
		}

		return first.distanceTo(second);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);

		first().validate(reporter.makeChild(".first"));
		second().validate(reporter.makeChild(".second"));

	}

}
