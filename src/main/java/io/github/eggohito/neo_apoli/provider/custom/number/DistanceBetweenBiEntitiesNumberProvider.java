package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class DistanceBetweenBiEntitiesNumberProvider extends NumberProvider {

	public static final MapCodec<DistanceBetweenBiEntitiesNumberProvider> CODEC = MapCodec.unit(DistanceBetweenBiEntitiesNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, DistanceBetweenBiEntitiesNumberProvider> PACKET_CODEC = PacketCodec.unit(new DistanceBetweenBiEntitiesNumberProvider());

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
		return ContextTypes.BIENTITY.getAllowed();
	}

}
