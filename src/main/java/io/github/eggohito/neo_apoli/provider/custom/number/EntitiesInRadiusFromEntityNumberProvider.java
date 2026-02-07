package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record EntitiesInRadiusFromEntityNumberProvider(BiEntityCondition biEntityCondition, ContextParameter<Entity> actor, Shape shape, NumberProvider radius) implements NumberProvider {

	private static final ContextKeySet CONDITION_CONTEXT = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.ACTOR_ENTITY)
		.required(NeoApoliContextParams.TARGET_ENTITY)
		.build();

	public static final MapCodec<EntitiesInRadiusFromEntityNumberProvider> MAP_CODEC = MapCodecUtil.lazy(EntitiesInRadiusFromEntityNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(EntitiesInRadiusFromEntityNumberProvider::biEntityCondition),
		NeoApoliCodecs.ENTITY_CONTEXT_PARAM.fieldOf("actor").forGetter(EntitiesInRadiusFromEntityNumberProvider::actor),
		Shape.CODEC.fieldOf("shape").forGetter(EntitiesInRadiusFromEntityNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(EntitiesInRadiusFromEntityNumberProvider::radius)
	).apply(instance, EntitiesInRadiusFromEntityNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntitiesInRadiusFromEntityNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(EntitiesInRadiusFromEntityNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		BiEntityCondition.STREAM_CODEC, EntitiesInRadiusFromEntityNumberProvider::biEntityCondition,
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, EntitiesInRadiusFromEntityNumberProvider::actor,
		Shape.STREAM_CODEC, EntitiesInRadiusFromEntityNumberProvider::shape,
		NumberProvider.STREAM_CODEC, EntitiesInRadiusFromEntityNumberProvider::radius,
		EntitiesInRadiusFromEntityNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ENTITIES_IN_RADIUS_FROM_ENTITY;
	}

	@Override
	public @NotNull Number next(Context context) {

		if (!context.hasParameter(actor())) {
			return 0;
		}

		Entity actor = context.getRequired(actor());
		Level level = actor.level();

		Context radiusContext = context.forChild(".radius");
		double radius = radius().nextDouble(radiusContext);

		if (radiusContext.hasErrors()) {
			return 0;
		}

		Vec3 pos = actor.position();
		int matches = 0;

		for (Entity target : shape().getEntities(level, pos, radius)) {

			Context biEntityContext = new Context.Builder(context)
				.withRequired(NeoApoliContextParams.ACTOR_ENTITY, actor)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, target)
				.build(level);

			if (biEntityCondition().test(biEntityContext.forChild(".bientity_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(actor());
	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		biEntityCondition().validate(validator.withAdditionalKeysFromSets(CONDITION_CONTEXT).forChild(".bientity_condition"));
		radius().validate(validator.forChild(".radius"));

	}

}
