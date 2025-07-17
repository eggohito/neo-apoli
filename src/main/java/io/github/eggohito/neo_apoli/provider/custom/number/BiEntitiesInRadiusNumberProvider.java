package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
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
public final class BiEntitiesInRadiusNumberProvider extends NumberProvider {

	public static final MapCodec<BiEntitiesInRadiusNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(BiEntitiesInRadiusNumberProvider::biEntityCondition),
		EntityParameter.CODEC.fieldOf("actor").forGetter(BiEntitiesInRadiusNumberProvider::actor),
		Shape.CODEC.fieldOf("shape").forGetter(BiEntitiesInRadiusNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(BiEntitiesInRadiusNumberProvider::radius)
	).apply(instance, BiEntitiesInRadiusNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, BiEntitiesInRadiusNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		BiEntityCondition.PACKET_CODEC, BiEntitiesInRadiusNumberProvider::biEntityCondition,
		EntityParameter.PACKET_CODEC, BiEntitiesInRadiusNumberProvider::actor,
		Shape.PACKET_CODEC, BiEntitiesInRadiusNumberProvider::shape,
		NumberProvider.PACKET_CODEC, BiEntitiesInRadiusNumberProvider::radius,
		BiEntitiesInRadiusNumberProvider::new
	);

	private final BiEntityCondition biEntityCondition;
	private final EntityParameter actor;

	private final Shape shape;
	private final NumberProvider radius;

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BIENTITIES_IN_RADIUS;
	}

	@Override
	protected Number impl(Context context) {

		Context radiusContext = context.makeChild(".radius");
		double radius = this.radius().nextDouble(radiusContext);

		if (radiusContext.hasErrors()) {
			return 0.0;
		}

		World world = context.getWorld();
		Entity actor = context.required(actor().getParameter());

		Vec3d pos = actor.getPos();
		int matches = 0;

		for (Entity target : this.shape().getEntities(world, pos, radius)) {

			Context biEntityContext = context.copy(builder -> builder
				.withContextType(ContextTypes.merge(context.getType(), ContextTypes.BIENTITY))
				.add(ContextParameters.ACTOR, actor)
				.add(ContextParameters.TARGET, target));

			if (this.biEntityCondition().test(biEntityContext.makeChild(".bientity_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(actor().getParameter());
	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		biEntityCondition().validate(reporter
			.withContextType(ContextTypes.merge(reporter.getContextType(), ContextTypes.BIENTITY))
			.makeChild(".bientity_condition"));
		radius().validate(reporter.makeChild(".radius"));

	}

}
