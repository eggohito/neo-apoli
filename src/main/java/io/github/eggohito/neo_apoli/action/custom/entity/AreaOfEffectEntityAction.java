package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.bientity.BiEntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextKeySetHelper;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record AreaOfEffectEntityAction(BiEntityAction biEntityAction, BiEntityCondition biEntityCondition, Shape shape, NumberProvider radius) implements EntityAction {

	public static final MapCodec<AreaOfEffectEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(AreaOfEffectEntityAction::biEntityAction),
		BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(AreaOfEffectEntityAction::biEntityCondition),
		Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(AreaOfEffectEntityAction::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(AreaOfEffectEntityAction::radius)
	).apply(instance, AreaOfEffectEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AreaOfEffectEntityAction> STREAM_CODEC = StreamCodec.composite(
		BiEntityAction.STREAM_CODEC, AreaOfEffectEntityAction::biEntityAction,
		BiEntityCondition.STREAM_CODEC, AreaOfEffectEntityAction::biEntityCondition,
		Shape.STREAM_CODEC, AreaOfEffectEntityAction::shape,
		NumberProvider.STREAM_CODEC, AreaOfEffectEntityAction::radius,
		AreaOfEffectEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.AREA_OF_EFFECT;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Level level = context.getLevel();
		Entity actor = context.required(NeoApoliContextKeys.THIS_ENTITY);

		Context radiusContext = context.forChild(".radius");
		double radius = radius().nextDouble(radiusContext);

		if (radiusContext.hasErrors() || Math.signum(radius) <= 0) {
			return;
		}

		for (var target : shape().getEntities(level, context.required(NeoApoliContextKeys.THIS_POS), radius)) {

			Context biEntityContext = new Context.Builder(context)
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BIENTITY))
				.add(NeoApoliContextKeys.ACTOR_ENTITY, actor)
				.add(NeoApoliContextKeys.TARGET_ENTITY, target)
				.build(level);

			if (biEntityCondition().test(biEntityContext.forChild(".bientity_condition"))) {
				biEntityAction().execute(biEntityContext.forChild(".bientity_action"));
			}

		}

	}

	@Override
	public void validate(ProblemReporter reporter) {

		EntityAction.super.validate(reporter);
		ProblemReporter biEntityReporter = reporter.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.BIENTITY));

		biEntityAction().validate(biEntityReporter.forChild(".bientity_action"));
		biEntityCondition().validate(biEntityReporter.forChild(".bientity_condition"));

		radius().validate(reporter.forChild(".radius"));

	}

}
