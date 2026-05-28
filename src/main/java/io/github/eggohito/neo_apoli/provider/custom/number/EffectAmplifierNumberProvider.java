package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record EffectAmplifierNumberProvider(Holder<MobEffect> effect, EntityProvider entity) implements NumberProvider {

	public static final MapCodec<EffectAmplifierNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		MobEffect.CODEC.fieldOf("effect").forGetter(EffectAmplifierNumberProvider::effect),
		EntityProvider.CODEC.fieldOf("entity").forGetter(EffectAmplifierNumberProvider::entity)
	).apply(instance, EffectAmplifierNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EffectAmplifierNumberProvider> STREAM_CODEC = StreamCodec.composite(
		MobEffect.STREAM_CODEC, EffectAmplifierNumberProvider::effect,
		EntityProvider.STREAM_CODEC, EffectAmplifierNumberProvider::entity,
		EffectAmplifierNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.EFFECT_AMPLIFIER;
	}

	@Override
	public double getDouble(Context context) {

		Context entityContext = context.forChild(".entity");
		Entity entity = entity().getEntity(entityContext).orElse(null);

		switch (entity) {
			case LivingEntity livingEntity when livingEntity.hasEffect(effect()) -> {
				return Objects.requireNonNull(livingEntity.getEffect(effect())).getAmplifier();
			}
			case LivingEntity ignored ->
				entityContext.reportProblem("Entity doesn't have the effect \"" + effect().unwrapKey().orElseThrow().location() + "\"!");
			case null ->
				entityContext.reportProblem("Entity doesn't exist!");
			default ->
				entityContext.reportProblem("Entity is not a living entity!");
		}

		return 0;

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		entity().validate(validator.forChild(".entity"));
	}

}
