package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.PowerReference;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record HasPowerEntityCondition(PowerReference power, Optional<ResourceLocation> source) implements EntityCondition {

	public static final MapCodec<HasPowerEntityCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerReference.CODEC.fieldOf("power").forGetter(HasPowerEntityCondition::power),
		ResourceLocation.CODEC.optionalFieldOf("source").forGetter(HasPowerEntityCondition::source)
	).apply(instance, HasPowerEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HasPowerEntityCondition> STREAM_CODEC = StreamCodec.composite(
		PowerReference.STREAM_CODEC, HasPowerEntityCondition::power,
		ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), HasPowerEntityCondition::source,
		HasPowerEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.HAS_POWER;
	}

	@Override
	public boolean test(Context context) {

		Powers powers = context.getOptional(NeoApoliContextParams.THIS_ENTITY)
			.flatMap(Powers::getOptional)
			.orElse(null);

		return powers != null
			&& this.testInternal(powers);

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityCondition.super.validate(validator);
		power().validate(validator.forChild(".power"));
	}

	private boolean testInternal(Powers powers) {
		return this.source()
			.map(innerSource -> powers.hasInstance(power(), innerSource))
			.orElseGet(() -> powers.hasInstance(power()));
	}

}
