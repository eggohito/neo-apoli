package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record IsPowerActiveEntityCondition(PowerReference power) implements EntityCondition {

	public static final MapCodec<IsPowerActiveEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(PowerReference.CODEC.fieldOf("power").forGetter(IsPowerActiveEntityCondition::power))
		.apply(instance, IsPowerActiveEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsPowerActiveEntityCondition> STREAM_CODEC = StreamCodec.composite(
		PowerReference.STREAM_CODEC, IsPowerActiveEntityCondition::power,
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

				Entity entity = context.required(NeoApoliContextKeys.THIS_ENTITY);
				PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.get(entity);

				return powersComponent.hasInstance(this.power())
					&& powersComponent.getInstance(this.power()).isActive(context.forChild(".power"));

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
	public void validate(Context.Validator validator) {
		EntityCondition.super.validate(validator);
		this.power().validate(validator.forChild(".power"));
	}

}
