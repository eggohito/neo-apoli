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
import net.minecraft.util.math.Vec3d;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class DistanceBetweenEntityAndBlockNumberProvider extends NumberProvider {

	public static final MapCodec<DistanceBetweenEntityAndBlockNumberProvider> CODEC = MapCodec.unit(DistanceBetweenEntityAndBlockNumberProvider::new);
	public static final PacketCodec<RegistryByteBuf, DistanceBetweenEntityAndBlockNumberProvider> PACKET_CODEC = PacketCodec.unit(new DistanceBetweenEntityAndBlockNumberProvider());

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.DISTANCE_BETWEEN_ENTITY_AND_BLOCK;
	}

	@Override
	protected Number impl(Context context) {

		Vec3d entityPos = context.required(ContextParameters.ENTITY_POS);
		Vec3d blockPos = context.required(ContextParameters.BLOCK_POS).toCenterPos();

		return entityPos.distanceTo(blockPos);

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.ENTITY_POS, ContextParameters.BLOCK_POS);
	}

}
