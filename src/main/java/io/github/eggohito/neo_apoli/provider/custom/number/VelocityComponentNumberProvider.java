package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.duck.MovingEntity;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record VelocityComponentNumberProvider(TypedContextKey<Entity> entity, Direction.Axis axis) implements NumberProvider {

	public static final MapCodec<VelocityComponentNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(VelocityComponentNumberProvider::entity),
		Direction.Axis.CODEC.fieldOf("axis").forGetter(VelocityComponentNumberProvider::axis)
	).apply(instance, VelocityComponentNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, VelocityComponentNumberProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, VelocityComponentNumberProvider::entity,
		NeoApoliStreamCodecs.AXIS, VelocityComponentNumberProvider::axis,
		VelocityComponentNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.VELOCITY_COMPONENT;
	}

	@Override
	public @NotNull Number next(Context context) {

		switch (context.nullable(entity())) {
			case MovingEntity movingEntity -> {
				return movingEntity.neo_apoli$getVelocity().get(axis());
			}
			case null ->
				context.getValidator().report("Couldn't get velocity from axis " + axis().getName() + ", as entity from parameter \"" + entity().name() + "\" doesn't exist!");
			default ->
				context.getValidator().report("Couldn't get velocity from axis " + axis().getName() + ", as entity from parameter \"" + entity().name() + "\" is not a moving entity!");
		}

		return 0.0D;

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
