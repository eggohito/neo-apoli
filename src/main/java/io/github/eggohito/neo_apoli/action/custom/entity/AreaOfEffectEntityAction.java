package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.bientity.BiEntityAction;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.action.NeoApoliEntityActionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.Shape;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record AreaOfEffectEntityAction(BiEntityAction biEntityAction, BiEntityCondition biEntityCondition, Shape shape, NumberProvider radius) implements EntityAction {

	public static final ContextKeySet BIENTITY_PARAMS = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.ACTOR_ENTITY)
		.required(NeoApoliContextParams.TARGET_ENTITY)
		.build();

	public static final MapCodec<AreaOfEffectEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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
	public EntityAction.Type<?> getType() {
		return NeoApoliEntityActionTypes.AREA_OF_EFFECT;
	}

	@Override
	public void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		Entity actor = context.getRequired(NeoApoliContextParams.THIS_ENTITY);
		Vec3 pos = context.getRequired(NeoApoliContextParams.THIS_POS);

		Level level = context.level();
		double radius = radius().nextDouble(context.forChild(".radius"));

		if (Math.signum(radius) <= 0) {
			return;
		}

		for (var target : shape().getEntities(level, pos, radius)) {

			Context biEntityContext = new Context.Builder(context)
				.withRequired(NeoApoliContextParams.ACTOR_ENTITY, actor)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, target)
				.build(level);

			if (biEntityCondition().test(biEntityContext.forChild(".bientity_condition"))) {
				biEntityAction().execute(biEntityContext.forChild(".bientity_action"));
			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {

		EntityAction.super.validate(validator);
		Context.Validator biEntityValidator = validator.withAdditionalKeysFromSets(BIENTITY_PARAMS);

		biEntityAction().validate(biEntityValidator.forChild(".bientity_action"));
		biEntityCondition().validate(biEntityValidator.forChild(".bientity_condition"));

		radius().validate(validator.forChild(".radius"));

	}

}
