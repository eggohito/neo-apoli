package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.entity.ConstantEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextKeySetHelper;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record EntitiesInRadiusFromPositionNumberProvider(EntityCondition entityCondition, Vec3Provider position, Shape shape, NumberProvider radius) implements NumberProvider {

	public static final MapCodec<EntitiesInRadiusFromPositionNumberProvider> CODEC = MapCodecUtil.lazy(EntitiesInRadiusFromPositionNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityCondition.CODEC.optionalFieldOf("entity_condition", new ConstantEntityCondition(true)).forGetter(EntitiesInRadiusFromPositionNumberProvider::entityCondition),
		Vec3Provider.CODEC.fieldOf("position").forGetter(EntitiesInRadiusFromPositionNumberProvider::position),
		Shape.CODEC.fieldOf("shape").forGetter(EntitiesInRadiusFromPositionNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(EntitiesInRadiusFromPositionNumberProvider::radius)
	).apply(instance, EntitiesInRadiusFromPositionNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntitiesInRadiusFromPositionNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(EntitiesInRadiusFromPositionNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		EntityCondition.STREAM_CODEC, EntitiesInRadiusFromPositionNumberProvider::entityCondition,
		Vec3Provider.STREAM_CODEC, EntitiesInRadiusFromPositionNumberProvider::position,
		Shape.STREAM_CODEC, EntitiesInRadiusFromPositionNumberProvider::shape,
		NumberProvider.STREAM_CODEC, EntitiesInRadiusFromPositionNumberProvider::radius,
		EntitiesInRadiusFromPositionNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ENTITIES_IN_RADIUS_FROM_POSITION;
	}

	@Override
	public @NotNull Number next(Context context) {

		Level level = context.getLevel();
		int matches = 0;

		Context positionContext = context.forChild(".position");
		Vec3 position = position().next(positionContext);

		if (positionContext.hasErrors()) {
			return matches;
		}

		Context radiusContext = context.forChild(".radius");
		double radius = radius().nextDouble(radiusContext);

		if (radiusContext.hasErrors()) {
			return matches;
		}

		for (var target : shape().getEntities(level, position, radius)) {

			Context entityContext = new Context.Builder(context)
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.ENTITY))
				.add(NeoApoliContextKeys.THIS_ENTITY, target)
				.add(NeoApoliContextKeys.THIS_POS, target.position())
				.build(level);

			if (entityCondition().test(entityContext.forChild(".entity_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);
		entityCondition().validate(validator
			.withKeySet(ContextKeySetHelper.merge(validator.getKeySet(), NeoApoliContextKeySets.ENTITY))
			.forChild(".entity_condition"));

		position().validate(validator.forChild(".position"));
		radius().validate(validator.forChild(".radius"));

	}

}
