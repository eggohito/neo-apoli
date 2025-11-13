package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.duck.MovingEntity;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record VelocityComponentNumberProvider(EntityTarget entity, Direction.Axis axis) implements NumberProvider {

	public static final MapCodec<VelocityComponentNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.fieldOf("entity").forGetter(VelocityComponentNumberProvider::entity),
		Direction.Axis.CODEC.fieldOf("axis").forGetter(VelocityComponentNumberProvider::axis)
	).apply(instance, VelocityComponentNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, VelocityComponentNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		EntityTarget.PACKET_CODEC, VelocityComponentNumberProvider::entity,
		NeoApoliPacketCodecs.AXIS, VelocityComponentNumberProvider::axis,
		VelocityComponentNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.VELOCITY_COMPONENT;
	}

	@Override
	public @NotNull Number next(Context context) {

		ContextParameter<Entity> parameter = entity().getParameter();
		Entity entity = context.nullable(parameter);

		switch (entity) {
			case MovingEntity movingEntity -> {
				return movingEntity.neo_apoli$getVelocity().getComponentAlongAxis(this.axis());
			}
			case null ->
				context.getReporter().report("Entity from parameter \"" + parameter.getId() + "\" doesn't exist!");
			default ->
				context.getReporter().report("Entity from parameter \"" + parameter.getId() + "\" is not a moving entity!");
		}

		return 0.0d;

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

}
