package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.custom.CooldownPower;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record PowerCooldownRemainingTicksNumberProvider(PowerIdentifier power, Context.Parameter<Entity> entity) implements NumberProvider {

	public static final MapCodec<PowerCooldownRemainingTicksNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerIdentifier.CODEC.fieldOf("power").forGetter(PowerCooldownRemainingTicksNumberProvider::power),
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(PowerCooldownRemainingTicksNumberProvider::entity)
	).apply(instance, PowerCooldownRemainingTicksNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PowerCooldownRemainingTicksNumberProvider> STREAM_CODEC = StreamCodec.composite(
		PowerIdentifier.STREAM_CODEC, PowerCooldownRemainingTicksNumberProvider::power,
		NeoApoliContextParams.StreamCodecs.ENTITY, PowerCooldownRemainingTicksNumberProvider::entity,
		PowerCooldownRemainingTicksNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.POWER_COOLDOWN_REMAINING_TICKS;
	}

	@Override
	public double nextDouble(Context context) {

		Entity entity = context.getNullable(entity());
		CooldownPower.Instance cooldownInstance = Powers.getOptional(entity)
			.flatMap(powers -> powers.getOptionalInstance(this.power()))
			.filter(CooldownPower.Instance.class::isInstance)
			.map(CooldownPower.Instance.class::cast)
			.orElse(null);

		if (entity == null || cooldownInstance == null) {

			if (entity == null) {
				context.reportProblem("Entity from parameter \"" + entity().name() + "\" doesn't exist!");
			}

			if (cooldownInstance == null) {
				context.reportProblem(power().asDisplayString() + " does not have a cooldown!");
			}

			return 0.0D;

		}

		else {
			return cooldownInstance.getRemainingTicks(context.forChild(".power"));
		}

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		power().validate(validator.forChild(".power"), CooldownPower.class, () -> power() + " doesn't have a cooldown!");
	}

}
