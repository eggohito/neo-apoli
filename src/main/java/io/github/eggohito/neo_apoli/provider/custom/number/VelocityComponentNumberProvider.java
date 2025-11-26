package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.duck.MovingEntity;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record VelocityComponentNumberProvider(EntityTarget entity, Direction.Axis axis) implements NumberProvider {

	public static final MapCodec<VelocityComponentNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityTarget.CODEC.fieldOf("entity").forGetter(VelocityComponentNumberProvider::entity),
		Direction.Axis.CODEC.fieldOf("axis").forGetter(VelocityComponentNumberProvider::axis)
	).apply(instance, VelocityComponentNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, VelocityComponentNumberProvider> STREAM_CODEC = StreamCodec.composite(
		EntityTarget.STREAM_CODEC, VelocityComponentNumberProvider::entity,
		NeoApoliStreamCodecs.AXIS, VelocityComponentNumberProvider::axis,
		VelocityComponentNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.VELOCITY_COMPONENT;
	}

	@Override
	public @NotNull Number next(Context context) {

		ContextKey<Entity> parameter = entity().getParameter();
		Entity entity = context.nullable(parameter);

		switch (entity) {
			case MovingEntity movingEntity -> {
				return movingEntity.neo_apoli$getVelocity().get(this.axis());
			}
			case null ->
				context.getReporter().report("Entity from parameter \"" + parameter.name() + "\" doesn't exist!");
			default ->
				context.getReporter().report("Entity from parameter \"" + parameter.name() + "\" is not a moving entity!");
		}

		return 0.0d;

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

}
