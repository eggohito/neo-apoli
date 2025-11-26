package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.*;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record EntitiesInRadiusFromEntityNumberProvider(BiEntityCondition biEntityCondition, TypedContextKey<Entity> actor, Shape shape, NumberProvider radius) implements NumberProvider {

	public static final MapCodec<EntitiesInRadiusFromEntityNumberProvider> CODEC = MapCodecUtil.lazy(EntitiesInRadiusFromEntityNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(EntitiesInRadiusFromEntityNumberProvider::biEntityCondition),
		NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("actor").forGetter(EntitiesInRadiusFromEntityNumberProvider::actor),
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

		Level world = context.getWorld();
		Entity actor = context.nullable(actor());

		if (actor == null) {
			return 0;
		}

		Context radiusContext = context.makeChild(".radius");
		double radius = radius().nextDouble(radiusContext);

		if (radiusContext.hasErrors()) {
			return 0;
		}

		Vec3 pos = actor.position();
		int matches = 0;

		for (Entity target : shape().getEntities(world, pos, radius)) {

			Context biEntityContext = ContextImpl.of(context, builder -> builder
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BIENTITY))
				.add(NeoApoliContextKeys.ACTOR, actor)
				.add(NeoApoliContextKeys.TARGET, target));

			if (biEntityCondition().test(biEntityContext.makeChild(".bientity_condition"))) {
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
	public void validate(ProblemReporter reporter) {

		NumberProvider.super.validate(reporter);
		biEntityCondition().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.BIENTITY))
			.forChild(".bientity_condition"));

		radius().validate(reporter.forChild(".radius"));

	}

}
