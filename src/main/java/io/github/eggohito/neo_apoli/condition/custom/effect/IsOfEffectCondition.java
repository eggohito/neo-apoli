package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;

public record IsOfEffectCondition(Holder<MobEffect> effect) implements EffectCondition {

	public static final MapCodec<IsOfEffectCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(MobEffect.CODEC.fieldOf("effect").forGetter(IsOfEffectCondition::effect))
		.apply(instance, IsOfEffectCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsOfEffectCondition> STREAM_CODEC = StreamCodec.composite(
		MobEffect.STREAM_CODEC, IsOfEffectCondition::effect,
		IsOfEffectCondition::new
	);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.IS_OF;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.EFFECT_INSTANCE)
			.map(instance -> instance.is(this.effect()))
			.orElse(false);
	}

}
