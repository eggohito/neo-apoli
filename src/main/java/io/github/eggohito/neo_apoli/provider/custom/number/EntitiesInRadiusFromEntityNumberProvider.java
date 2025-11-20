package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record EntitiesInRadiusFromEntityNumberProvider(BiEntityCondition biEntityCondition, EntityTarget actor, Shape shape, NumberProvider radius) implements NumberProvider {

	public static final MapCodec<EntitiesInRadiusFromEntityNumberProvider> CODEC = MapCodecUtil.lazy(EntitiesInRadiusFromEntityNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(EntitiesInRadiusFromEntityNumberProvider::biEntityCondition),
		EntityTarget.CODEC.fieldOf("actor").forGetter(EntitiesInRadiusFromEntityNumberProvider::actor),
		Shape.CODEC.fieldOf("shape").forGetter(EntitiesInRadiusFromEntityNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(EntitiesInRadiusFromEntityNumberProvider::radius)
	).apply(instance, EntitiesInRadiusFromEntityNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, EntitiesInRadiusFromEntityNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(EntitiesInRadiusFromEntityNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		BiEntityCondition.PACKET_CODEC, EntitiesInRadiusFromEntityNumberProvider::biEntityCondition,
		EntityTarget.PACKET_CODEC, EntitiesInRadiusFromEntityNumberProvider::actor,
		Shape.PACKET_CODEC, EntitiesInRadiusFromEntityNumberProvider::shape,
		NumberProvider.PACKET_CODEC, EntitiesInRadiusFromEntityNumberProvider::radius,
		EntitiesInRadiusFromEntityNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ENTITIES_IN_RADIUS_FROM_ENTITY;
	}

	@Override
	public @NotNull Number next(Context context) {

		World world = context.getWorld();
		Entity actor = context.nullable(actor().getParameter());

		if (actor == null) {
			return 0;
		}

		Context radiusContext = context.makeChild(".radius");
		double radius = radius().nextDouble(radiusContext);

		if (radiusContext.hasErrors()) {
			return 0;
		}

		Vec3d pos = actor.getPos();
		int matches = 0;

		for (Entity target : shape().getEntities(world, pos, radius)) {

			Context biEntityContext = ContextImpl.of(context, builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), NeoApoliContextTypes.BIENTITY))
				.add(NeoApoliContextParameters.ACTOR, actor)
				.add(NeoApoliContextParameters.TARGET, target));

			if (biEntityCondition().test(biEntityContext.makeChild(".bientity_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(actor().getParameter());
	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);
		biEntityCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), NeoApoliContextTypes.BIENTITY))
			.makeChild(".bientity_condition"));

		radius().validate(reporter.makeChild(".radius"));

	}

}
