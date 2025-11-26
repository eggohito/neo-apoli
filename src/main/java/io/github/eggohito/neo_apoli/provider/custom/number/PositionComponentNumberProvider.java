package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record PositionComponentNumberProvider(Vec3dProvider position, Direction.Axis axis) implements NumberProvider {

	public static final MapCodec<PositionComponentNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3dProvider.CODEC.fieldOf("position").forGetter(PositionComponentNumberProvider::position),
		Direction.Axis.CODEC.fieldOf("axis").forGetter(PositionComponentNumberProvider::axis)
	).apply(instance, PositionComponentNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PositionComponentNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Vec3dProvider.STREAM_CODEC, PositionComponentNumberProvider::position,
		NeoApoliStreamCodecs.AXIS, PositionComponentNumberProvider::axis,
		PositionComponentNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.POSITION_COMPONENT;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context positionContext = context.makeChild(".position");
		Vec3 position = position().next(positionContext);

		if (positionContext.hasErrors()) {
			return 0.0d;
		}

		else {
			return position.get(this.axis());
		}

	}

	@Override
	public void validate(ProblemReporter reporter) {
		NumberProvider.super.validate(reporter);
		position().validate(reporter.forChild(".position"));
	}

}
