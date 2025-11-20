package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.entity.ConstantEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public record EntitiesInRadiusFromPositionNumberProvider(EntityCondition entityCondition, Vec3dProvider position, Shape shape, NumberProvider radius) implements NumberProvider {

	public static final MapCodec<EntitiesInRadiusFromPositionNumberProvider> CODEC = MapCodecUtil.lazy(EntitiesInRadiusFromPositionNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityCondition.CODEC.optionalFieldOf("entity_condition", new ConstantEntityCondition(true)).forGetter(EntitiesInRadiusFromPositionNumberProvider::entityCondition),
		Vec3dProvider.CODEC.fieldOf("position").forGetter(EntitiesInRadiusFromPositionNumberProvider::position),
		Shape.CODEC.fieldOf("shape").forGetter(EntitiesInRadiusFromPositionNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(EntitiesInRadiusFromPositionNumberProvider::radius)
	).apply(instance, EntitiesInRadiusFromPositionNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, EntitiesInRadiusFromPositionNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(EntitiesInRadiusFromPositionNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		EntityCondition.PACKET_CODEC, EntitiesInRadiusFromPositionNumberProvider::entityCondition,
		Vec3dProvider.PACKET_CODEC, EntitiesInRadiusFromPositionNumberProvider::position,
		Shape.PACKET_CODEC, EntitiesInRadiusFromPositionNumberProvider::shape,
		NumberProvider.PACKET_CODEC, EntitiesInRadiusFromPositionNumberProvider::radius,
		EntitiesInRadiusFromPositionNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ENTITIES_IN_RADIUS_FROM_POSITION;
	}

	@Override
	public @NotNull Number next(Context context) {

		World world = context.getWorld();
		int matches = 0;

		Context positionContext = context.makeChild(".position");
		Vec3d position = position().next(positionContext);

		if (positionContext.hasErrors()) {
			return matches;
		}

		Context radiusContext = context.makeChild(".radius");
		double radius = radius().nextDouble(radiusContext);

		if (radiusContext.hasErrors()) {
			return matches;
		}

		for (var target : shape().getEntities(world, position, radius)) {

			Context entityContext = ContextImpl.of(context, builder -> builder
				.add(NeoApoliContextParameters.THIS_ENTITY, target)
				.add(NeoApoliContextParameters.ENTITY_POS, target.getPos()));

			if (entityCondition().test(entityContext.makeChild(".entity_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);
		entityCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), NeoApoliContextTypes.ENTITY))
			.makeChild(".entity_condition"));

		position().validate(reporter.makeChild(".position"));
		radius().validate(reporter.makeChild(".radius"));

	}

}
