package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.ConstantCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record EntitiesInRadiusNumberProvider(Condition condition, Vec3Provider position, Shape shape, NumberProvider radius) implements NumberProvider {

	private static final ContextKeySet CONDITION_PARAMETER_SET = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.TARGET_ENTITY)
		.build();

	public static final MapCodec<EntitiesInRadiusNumberProvider> CODEC = MapCodecUtil.lazy(EntitiesInRadiusNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		Condition.CODEC.optionalFieldOf("condition", new ConstantCondition(true)).forGetter(EntitiesInRadiusNumberProvider::condition),
		Vec3Provider.CODEC.fieldOf("position").forGetter(EntitiesInRadiusNumberProvider::position),
		Shape.CODEC.fieldOf("shape").forGetter(EntitiesInRadiusNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(EntitiesInRadiusNumberProvider::radius)
	).apply(instance, EntitiesInRadiusNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntitiesInRadiusNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(EntitiesInRadiusNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		Condition.STREAM_CODEC, EntitiesInRadiusNumberProvider::condition,
		Vec3Provider.STREAM_CODEC, EntitiesInRadiusNumberProvider::position,
		Shape.STREAM_CODEC, EntitiesInRadiusNumberProvider::shape,
		NumberProvider.STREAM_CODEC, EntitiesInRadiusNumberProvider::radius,
		EntitiesInRadiusNumberProvider::new
	));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ENTITIES_IN_RADIUS;
	}

	@Override
	public double getDouble(Context context) {

		Level level = context.level();
		int matches = 0;

		Vec3 position = position()
			.getVec3(context.forChild(".position"))
			.orElse(null);

		if (position == null) {
			return matches;
		}

		Context radiusContext = context.forChild(".radius");
		double radius = radius().getDouble(radiusContext);

		if (radiusContext.hasErrors()) {
			return matches;
		}

		for (var target : shape().getEntities(level, position, radius)) {

			Context entityContext = new Context.Builder(context)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, target)
				.build(level);

			if (condition().test(entityContext.forChild(".condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		condition().validate(validator.withAdditionalKeysFromSets(CONDITION_PARAMETER_SET).forChild(".condition"));
		position().validate(validator.forChild(".position"));
		radius().validate(validator.forChild(".radius"));
	}

}
