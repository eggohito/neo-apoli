package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record IsPowerActiveEntityCondition(PowerReference power) implements EntityCondition {

	public static final MapCodec<IsPowerActiveEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(PowerReference.CODEC.fieldOf("power").forGetter(IsPowerActiveEntityCondition::power))
		.apply(instance, IsPowerActiveEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, IsPowerActiveEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		PowerReference.PACKET_CODEC, IsPowerActiveEntityCondition::power,
		IsPowerActiveEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_POWER_ACTIVE;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		try {

			if (context.markActive(this)) {

				Entity entity = context.required(NeoApoliContextParameters.THIS_ENTITY);
				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(entity);

				return powersComponent.hasInstance(this.power())
					&& powersComponent.getInstance(this.power()).isActive(context.makeChild(".power"));

			}

			else {
				return false;
			}

		}

		finally {
			context.markInActive(this);
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		EntityCondition.super.validate(reporter);
		PowerManager.getAsResult(this.power()).ifError(error -> reporter.makeChild(".power").report(error.message()));
	}

}
