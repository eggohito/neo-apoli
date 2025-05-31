package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Set;

public record EntitiesInRadiusNumberProvider(BiEntityCondition biEntityCondition, Shape shape, NumberProvider radius) implements NumberProvider {

	public static final MapCodec<EntitiesInRadiusNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(EntitiesInRadiusNumberProvider::biEntityCondition),
		Shape.CODEC.fieldOf("shape").forGetter(EntitiesInRadiusNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(EntitiesInRadiusNumberProvider::radius)
	).apply(instance, EntitiesInRadiusNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, EntitiesInRadiusNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		BiEntityCondition.PACKET_CODEC, EntitiesInRadiusNumberProvider::biEntityCondition,
		Shape.PACKET_CODEC, EntitiesInRadiusNumberProvider::shape,
		NumberProvider.PACKET_CODEC, EntitiesInRadiusNumberProvider::radius,
		EntitiesInRadiusNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ENTITIES_IN_RADIUS;
	}

	@Override
	public double doubleValue(Context context) {

		Context radiusContext = context.makeChild("radius");
		double radius = this.radius().doubleValue(radiusContext);

		if (radiusContext.hasErrors()) {
			return 0.0;
		}

		World world = context.getWorld();
		Vec3d centerPos = context.requiredParameter(ContextParameters.POSITION);

		Context.Builder builder = new Context.Builder(context).add(ContextParameters.ACTOR, context.requiredParameter(ContextParameters.THIS_ENTITY));
		int matches = 0;

		for (Entity target : this.shape().getEntities(world, centerPos, radius)) {

			Context biEntityConditionContext = builder
				.add(ContextParameters.TARGET, target)
				.build(context.getWorld())
				.makeChild("bientity_condition");

			if (this.biEntityCondition().test(biEntityConditionContext)) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION, ContextParameters.THIS_ENTITY);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);

		biEntityCondition().validate(reporter.makeChild("bientity_condition"));
		radius().validate(reporter.makeChild("radius"));

	}

}
