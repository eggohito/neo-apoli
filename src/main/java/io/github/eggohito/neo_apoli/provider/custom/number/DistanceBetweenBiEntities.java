package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
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

import java.util.Set;

@EqualsAndHashCode
@Data
public final class DistanceBetweenBiEntities extends NumberProvider {

	public static final MapCodec<DistanceBetweenBiEntities> CODEC = MapCodec.unit(DistanceBetweenBiEntities::new);
	public static final PacketCodec<RegistryByteBuf, DistanceBetweenBiEntities> PACKET_CODEC = PacketCodec.unit(new DistanceBetweenBiEntities());

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DISTANCE_BETWEEN_BIENTITIES;
	}

	@Override
	protected Number impl(Context context) {
		return context.required(ContextParameters.ACTOR).distanceTo(context.required(ContextParameters.TARGET));
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.ACTOR, ContextParameters.TARGET);
	}

}
