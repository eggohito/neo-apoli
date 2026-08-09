package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.custom.CooldownStandalonePower;
import io.github.eggohito.neo_apoli.power.custom.misc.CooldownPower;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record PowerCooldownProgressNumberProvider(PowerIdentifier power, EntityProvider entity) implements NumberProvider {

	public static final MapCodec<PowerCooldownProgressNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerIdentifier.CODEC.fieldOf("power").forGetter(PowerCooldownProgressNumberProvider::power),
		EntityProvider.CODEC.fieldOf("entity").forGetter(PowerCooldownProgressNumberProvider::entity)
	).apply(instance, PowerCooldownProgressNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PowerCooldownProgressNumberProvider> STREAM_CODEC = StreamCodec.composite(
		PowerIdentifier.STREAM_CODEC, PowerCooldownProgressNumberProvider::power,
		EntityProvider.STREAM_CODEC, PowerCooldownProgressNumberProvider::entity,
		PowerCooldownProgressNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.POWER_COOLDOWN_PROGRESS;
	}

	@Override
	public double getDouble(Context context) {

		Context entityContext = context.forChild(".entity");
		Entity entity = entity().getEntity(entityContext).orElse(null);

		CooldownPower.Instance<?> cooldownInstance = Powers.getOptional(entity)
			.flatMap(powers -> powers.getOptionalInstance(this.power()))
			.filter(CooldownPower.Instance.class::isInstance)
			.map(CooldownPower.Instance.class::cast)
			.orElse(null);

		if (entity == null || cooldownInstance == null) {

			if (entity == null) {
				entityContext.reportProblem("Entity doesn't exist!");
			}

			if (cooldownInstance == null) {
				context.reportProblem(power().asDisplayString() + " does not have a cooldown!");
			}

			return 0.0D;

		}

		else {
			return cooldownInstance.getProgress(context.forChild(".power"));
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		power().validate(validator.forChild(".power"), CooldownStandalonePower.class, () -> power().asDisplayString() + " doesn't have a cooldown!");
		entity().validate(validator.forChild(".entity"));
	}

}
