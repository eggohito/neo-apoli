package io.github.eggohito.neo_apoli.condition.custom.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionType;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public record IsInTagEffectCondition(TagKey<MobEffect> tag) implements EffectCondition {

	public static final MapCodec<IsInTagEffectCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(CodecUtil.hashedTag(Registries.MOB_EFFECT).fieldOf("tag").forGetter(IsInTagEffectCondition::tag))
		.apply(instance, IsInTagEffectCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInTagEffectCondition> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.MOB_EFFECT), IsInTagEffectCondition::tag,
		IsInTagEffectCondition::new
	);

	@Override
	public EffectConditionType<?> getType() {
		return EffectConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.EFFECT_INSTANCE)
			.map(MobEffectInstance::getEffect)
			.map(effect -> effect.is(this.tag()))
			.orElse(false);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		EffectCondition.super.validate(reporter);
		RegistryUtil.validateTag(reporter.forChild(".tag"), this.tag());
	}

}
