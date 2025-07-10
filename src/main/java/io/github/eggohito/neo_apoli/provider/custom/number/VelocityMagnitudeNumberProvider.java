package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.duck.MovingEntity;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

@EqualsAndHashCode
@Data
public class VelocityMagnitudeNumberProvider extends NumberProvider {

	public static final MapCodec<VelocityMagnitudeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityParameter.CODEC.fieldOf("entity").forGetter(VelocityMagnitudeNumberProvider::entity)
	).apply(instance, VelocityMagnitudeNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, VelocityMagnitudeNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		EntityParameter.PACKET_CODEC, VelocityMagnitudeNumberProvider::entity,
		VelocityMagnitudeNumberProvider::new
	);

	private final EntityParameter entity;

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.VELOCITY_MAGNITUDE;
	}

	@Override
	protected Number impl(Context context) {

		ContextParameter<Entity> parameter = entity().getParameter();
		Entity entity = context.required(entity().getParameter());

		if (entity instanceof MovingEntity movingEntity) {
			return Math.sqrt(movingEntity.neo_apoli$getSquaredVelocityMagnitude());
		}

		else {
			context.getReporter().report("Entity from parameter \"" + parameter.getId() + "\" is not a moving entity!");
			return 0.0d;
		}

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(entity().getParameter());
	}

}
