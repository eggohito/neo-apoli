package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public record EffectAmplifierNumberProvider(Holder<MobEffect> effect, ContextParameter<Entity> entity) implements NumberProvider {

	public static final MapCodec<EffectAmplifierNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		MobEffect.CODEC.fieldOf("effect").forGetter(EffectAmplifierNumberProvider::effect),
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(EffectAmplifierNumberProvider::entity)
	).apply(instance, EffectAmplifierNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EffectAmplifierNumberProvider> STREAM_CODEC = StreamCodec.composite(
		MobEffect.STREAM_CODEC, EffectAmplifierNumberProvider::effect,
		NeoApoliContextParams.StreamCodecs.ENTITY, EffectAmplifierNumberProvider::entity,
		EffectAmplifierNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.EFFECT_AMPLIFIER;
	}

	@Override
	public double nextDouble(Context context) {

		switch (context.getNullable(entity())) {
			case LivingEntity livingEntity when livingEntity.hasEffect(effect()) -> {
				return Objects.requireNonNull(livingEntity.getEffect(effect())).getAmplifier();
			}
			case LivingEntity ignored ->
				context.reportProblem("Entity from parameter \"" + entity().name() + "\" doesn't have the effect \"" + effect().unwrapKey().orElseThrow().location() + "\"!");
			case null ->
				context.reportProblem("Entity from parameter \"" + entity().name() + "\" doesn't exist!");
			default ->
				context.reportProblem("Entity from parameter \"" + entity().name() + "\" is not a living entity!");
		}

		return 0;

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
