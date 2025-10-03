package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class EntitiesInRadiusNumberProvider extends NumberProvider {

	public static final MapCodec<EntitiesInRadiusNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityCondition.CODEC.fieldOf("entity_condition").forGetter(EntitiesInRadiusNumberProvider::entityCondition),
		Shape.CODEC.fieldOf("shape").forGetter(EntitiesInRadiusNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(EntitiesInRadiusNumberProvider::radius)
	).apply(instance, EntitiesInRadiusNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, EntitiesInRadiusNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		EntityCondition.PACKET_CODEC, EntitiesInRadiusNumberProvider::entityCondition,
		Shape.PACKET_CODEC, EntitiesInRadiusNumberProvider::shape,
		NumberProvider.PACKET_CODEC, EntitiesInRadiusNumberProvider::radius,
		EntitiesInRadiusNumberProvider::new
	);

	private final EntityCondition entityCondition;

	private final Shape shape;
	private final NumberProvider radius;

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ENTITIES_IN_RADIUS;
	}

	@Override
	protected Number impl(Context context) {

		Context radiusContext = context.makeChild(".radius");
		double radius = this.radius().nextDouble(radiusContext);

		if (radiusContext.hasErrors()) {
			return 0.0;
		}

		World world = context.getWorld();
		Vec3d pos = context.required(ContextParameters.POSITION);

		int matches = 0;

		for (Entity target : this.shape().getEntities(world, pos, radius)) {

			Context entityContext = new ContextImpl.Builder(context)
				.add(ContextParameters.ENTITY, target)
				.add(ContextParameters.ENTITY_POS, target.getPos())
				.build(world);

			if (this.entityCondition().test(entityContext.makeChild(".entity_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.POSITION);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		entityCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.ENTITY))
			.makeChild(".entity_condition"));
		radius().validate(reporter.makeChild(".radius"));

	}

}
