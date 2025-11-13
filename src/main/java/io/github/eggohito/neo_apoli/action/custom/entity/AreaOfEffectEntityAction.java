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
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.world.World;

public record AreaOfEffectEntityAction(BiEntityAction biEntityAction, BiEntityCondition biEntityCondition, Shape shape, NumberProvider radius) implements EntityAction {

	public static final MapCodec<AreaOfEffectEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(AreaOfEffectEntityAction::biEntityAction),
		BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(AreaOfEffectEntityAction::biEntityCondition),
		Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(AreaOfEffectEntityAction::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(AreaOfEffectEntityAction::radius)
	).apply(instance, AreaOfEffectEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, AreaOfEffectEntityAction> PACKET_CODEC = PacketCodec.tuple(
		BiEntityAction.PACKET_CODEC, AreaOfEffectEntityAction::biEntityAction,
		BiEntityCondition.PACKET_CODEC, AreaOfEffectEntityAction::biEntityCondition,
		Shape.PACKET_CODEC, AreaOfEffectEntityAction::shape,
		NumberProvider.PACKET_CODEC, AreaOfEffectEntityAction::radius,
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

		World world = context.getWorld();
		Entity actor = context.required(ContextParameters.THIS_ENTITY);

		Context radiusContext = context.makeChild(".radius");
		double radius = radius().nextDouble(radiusContext);

		if (radiusContext.hasErrors() || Math.signum(radius) <= 0) {
			return;
		}

		for (var target : shape().getEntities(world, context.required(ContextParameters.ENTITY_POS), radius)) {

			Context biEntityContext = ContextImpl.of(context, builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BIENTITY))
				.add(ContextParameters.ACTOR, actor)
				.add(ContextParameters.TARGET, target));

			if (biEntityCondition().test(biEntityContext.makeChild(".bientity_condition"))) {
				biEntityAction().execute(biEntityContext.makeChild(".bientity_action"));
			}

		}

	}

	@Override
	public void validate(ErrorReporter reporter) {

		EntityAction.super.validate(reporter);
		ErrorReporter biEntityReporter = reporter.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.BIENTITY));

		biEntityAction().validate(biEntityReporter.makeChild(".bientity_action"));
		biEntityCondition().validate(biEntityReporter.makeChild(".bientity_condition"));

		radius().validate(reporter.makeChild(".radius"));

	}

}
