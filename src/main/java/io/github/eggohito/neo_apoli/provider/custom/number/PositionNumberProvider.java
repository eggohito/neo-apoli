package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Direction;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class PositionNumberProvider extends NumberProvider {

	public static final MapCodec<PositionNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Direction.Axis.CODEC.fieldOf("axis").forGetter(PositionNumberProvider::axis)
	).apply(instance, PositionNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, PositionNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.AXIS, PositionNumberProvider::axis,
		PositionNumberProvider::new
	);

	private final Direction.Axis axis;

	public PositionNumberProvider(Direction.Axis axis) {
		this.axis = axis;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.POSITION;
	}

	@Override
	protected Number impl(Context context) {
		return context.required(ContextParameters.POSITION).getComponentAlongAxis(this.axis());
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.POSITION);
	}

}
