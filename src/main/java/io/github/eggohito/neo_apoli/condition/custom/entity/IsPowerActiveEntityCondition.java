package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;

public record IsPowerActiveEntityCondition(PowerIdentifier power) implements EntityCondition {

	public static final MapCodec<IsPowerActiveEntityCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(PowerIdentifier.CODEC.fieldOf("power").forGetter(IsPowerActiveEntityCondition::power))
		.apply(instance, IsPowerActiveEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsPowerActiveEntityCondition> STREAM_CODEC = StreamCodec.composite(
		PowerIdentifier.STREAM_CODEC, IsPowerActiveEntityCondition::power,
		IsPowerActiveEntityCondition::new
	);

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.IS_POWER_ACTIVE;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		try {

			if (context.visitor().push(this)) {

				Entity entity = context.getRequired(NeoApoliContextParams.THIS_ENTITY);
				Powers powers = Powers.getNullable(entity);

				return powers != null
					&& powers.hasInstance(this.power())
					&& powers.getInstance(this.power()).isActive(context.forChild(".power"));

			}

			else {
				return false;
			}

		}

		finally {
			context.visitor().pop(this);
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityCondition.super.validate(validator);
		this.power().validate(validator.forChild(".power"));
	}

}
