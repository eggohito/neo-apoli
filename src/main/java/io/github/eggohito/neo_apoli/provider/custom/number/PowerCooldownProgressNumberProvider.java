package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.power.PowerReference;
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

public record PowerCooldownProgressNumberProvider(PowerReference power, ContextParameter<Entity> entity) implements NumberProvider {

	public static final MapCodec<PowerCooldownProgressNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerReference.CODEC.fieldOf("power").forGetter(PowerCooldownProgressNumberProvider::power),
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(PowerCooldownProgressNumberProvider::entity)
	).apply(instance, PowerCooldownProgressNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PowerCooldownProgressNumberProvider> STREAM_CODEC = StreamCodec.composite(
		PowerReference.STREAM_CODEC, PowerCooldownProgressNumberProvider::power,
		NeoApoliContextParams.StreamCodecs.ENTITY, PowerCooldownProgressNumberProvider::entity,
		PowerCooldownProgressNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.POWER_COOLDOWN_PROGRESS;
	}

	@Override
	public @NotNull Number nextNumber(Context context) {

		Entity entity = context.getNullable(entity());
		CooldownPower.Instance cooldownInstance = NeoApoliEntityComponents.POWERS.maybeGet(entity)
			.flatMap(powersComponent -> powersComponent.getOptionalInstance(this.power()))
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
			return cooldownInstance.getProgress(context.forChild(".power"));
		}

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		power().validate(validator.forChild(".power"), CooldownPower.class, () -> power().asDisplayString() + " doesn't have a cooldown!");
	}

}
