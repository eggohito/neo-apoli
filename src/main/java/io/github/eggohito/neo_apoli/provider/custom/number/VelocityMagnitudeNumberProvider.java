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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record VelocityMagnitudeNumberProvider(TypedContextKey<Entity> entity) implements NumberProvider {

	public static final MapCodec<VelocityMagnitudeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(VelocityMagnitudeNumberProvider::entity)
	).apply(instance, VelocityMagnitudeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, VelocityMagnitudeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, VelocityMagnitudeNumberProvider::entity,
		VelocityMagnitudeNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.VELOCITY_MAGNITUDE;
	}

	@Override
	public @NotNull Number next(Context context) {

		switch (context.nullable(entity())) {
			case MovingEntity movingEntity -> {
				return Math.sqrt(movingEntity.neo_apoli$getSquaredVelocityMagnitude());
			}
			case null ->
				context.getValidator().report("Couldn't get velocity magnitude of entity from parameter \"" + entity().name() + "\", as it doesn't exist!");
			default ->
				context.getValidator().report("Couldn't get velocity magnitude of entity from parameter \"" + entity().name() + "\", as it's not a moving entity!");
		}

		return 0.0D;

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
