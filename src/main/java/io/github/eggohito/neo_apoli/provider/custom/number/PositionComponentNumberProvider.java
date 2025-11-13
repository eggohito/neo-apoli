package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public record PositionComponentNumberProvider(Vec3dProvider position, Direction.Axis axis) implements NumberProvider {

	public static final MapCodec<PositionComponentNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3dProvider.CODEC.fieldOf("position").forGetter(PositionComponentNumberProvider::position),
		Direction.Axis.CODEC.fieldOf("axis").forGetter(PositionComponentNumberProvider::axis)
	).apply(instance, PositionComponentNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, PositionComponentNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		Vec3dProvider.PACKET_CODEC, PositionComponentNumberProvider::position,
		NeoApoliPacketCodecs.AXIS, PositionComponentNumberProvider::axis,
		PositionComponentNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.POSITION_COMPONENT;
	}

	@Override
	public @NotNull Number next(Context context) {

		Context positionContext = context.makeChild(".position");
		Vec3d position = position().next(positionContext);

		if (positionContext.hasErrors()) {
			return 0.0d;
		}

		else {
			return position.getComponentAlongAxis(this.axis());
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter);
		position().validate(reporter.makeChild(".position"));
	}

}
