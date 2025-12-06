package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.CooldownPower;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record CooldownProgressNumberProvider(PowerReference power, TypedContextKey<Entity> entity) implements NumberProvider {

	public static final MapCodec<CooldownProgressNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		PowerReference.CODEC.fieldOf("power").forGetter(CooldownProgressNumberProvider::power),
		NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(CooldownProgressNumberProvider::entity)
	).apply(instance, CooldownProgressNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CooldownProgressNumberProvider> STREAM_CODEC = StreamCodec.composite(
		PowerReference.STREAM_CODEC, CooldownProgressNumberProvider::power,
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, CooldownProgressNumberProvider::entity,
		CooldownProgressNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.COOLDOWN_PROGRESS;
	}

	@Override
	public @NotNull Number next(Context context) {

		Entity entity = context.nullable(entity());
		PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.maybeGet(entity).orElse(null);

		if (powersComponent == null || !(powersComponent.getNullableInstance(power()) instanceof CooldownPower.Instance cooldownInstance)) {
			return 0.0D;
		}

		else {
			return cooldownInstance.getProgress(context);
		}

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	public void validate(ProblemReporter reporter) {
		NumberProvider.super.validate(reporter);
		CooldownPower.getAsResult(power()).ifError(error -> reporter.report(error.message()));
	}

}
