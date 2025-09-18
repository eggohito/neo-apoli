package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
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
import net.minecraft.util.math.Direction;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class VelocityNumberProvider extends NumberProvider {

	public static final MapCodec<VelocityNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityParameter.CODEC.fieldOf("entity").forGetter(VelocityNumberProvider::entity),
		Direction.Axis.CODEC.fieldOf("axis").forGetter(VelocityNumberProvider::axis)
	).apply(instance, VelocityNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, VelocityNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		EntityParameter.PACKET_CODEC, VelocityNumberProvider::entity,
		NeoApoliPacketCodecs.AXIS, VelocityNumberProvider::axis,
		VelocityNumberProvider::new
	);

	private final EntityParameter entity;
	private final Direction.Axis axis;

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.VELOCITY;
	}

	@Override
	protected Number impl(Context context) {

		ContextParameter<Entity> parameter = entity().getParameter();
		Entity entity = context.required(parameter);

		if (entity instanceof MovingEntity movingEntity) {
			return movingEntity.neo_apoli$getVelocity().getComponentAlongAxis(axis());
		}

		else {
			context.getReporter().report("Entity from parameter \"" + parameter.getId() + "\" is not a moving entity!");
			return 0.0;
		}

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

}
