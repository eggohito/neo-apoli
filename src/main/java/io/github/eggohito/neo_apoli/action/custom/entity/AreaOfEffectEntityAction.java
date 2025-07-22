package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@EqualsAndHashCode
@Data
public final class AreaOfEffectEntityAction extends EntityAction {

	public static final MapCodec<AreaOfEffectEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityAction.CODEC.fieldOf("bientity_action").forGetter(AreaOfEffectEntityAction::biEntityAction),
		BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(AreaOfEffectEntityAction::biEntityCondition),
		Shape.CODEC.fieldOf("shape").forGetter(AreaOfEffectEntityAction::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(AreaOfEffectEntityAction::radius)
	).apply(instance, AreaOfEffectEntityAction::new));

	public static final PacketCodec<RegistryByteBuf, AreaOfEffectEntityAction> PACKET_CODEC = PacketCodec.tuple(
		BiEntityAction.PACKET_CODEC, AreaOfEffectEntityAction::biEntityAction,
		BiEntityCondition.PACKET_CODEC, AreaOfEffectEntityAction::biEntityCondition,
		Shape.PACKET_CODEC, AreaOfEffectEntityAction::shape,
		NumberProvider.PACKET_CODEC, AreaOfEffectEntityAction::radius,
		AreaOfEffectEntityAction::new
	);

	private final BiEntityAction biEntityAction;
	private final BiEntityCondition biEntityCondition;

	private final Shape shape;
	private final NumberProvider radius;

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.AREA_OF_EFFECT;
	}

	@Override
	protected void impl(Context context) {

		World world = context.getWorld();
		Vec3d originPos = context.required(ContextParameters.ENTITY_POS);

		Context radiusContext = context.makeChild(".radius");
		double radius = radius().nextDouble(radiusContext);

		if (radiusContext.hasErrors() || Math.signum(radius) <= 0) {
			return;
		}

		for (Entity target : shape().getEntities(world, originPos, radius)) {

			Context biEntityContext = context.copy(builder -> builder
				.withContextType(ContextTypes.merge(context.getType(), ContextTypes.BIENTITY))
				.add(ContextParameters.ACTOR, context.required(ContextParameters.ENTITY))
				.add(ContextParameters.TARGET, target));

			if (biEntityCondition().test(biEntityContext.makeChild(".bientity_condition"))) {
				biEntityAction().execute(biEntityContext.makeChild(".bientity_action"));
			}

		}

	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);
		ErrorReporter biEntityReporter = reporter.withContextType(ContextTypes.merge(reporter.getContextType(), ContextTypes.BIENTITY));

		biEntityAction().validate(biEntityReporter.makeChild(".bientity_action"));
		biEntityCondition().validate(biEntityReporter.makeChild(".bientity_condition"));

		radius().validate(reporter.makeChild(".radius"));

	}

}
