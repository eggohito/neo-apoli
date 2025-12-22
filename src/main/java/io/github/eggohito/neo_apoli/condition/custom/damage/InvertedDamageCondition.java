package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.IInvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedDamageCondition(DamageCondition condition) implements DamageCondition, IInvertedMetaCondition<DamageCondition> {

	public static final MapCodec<InvertedDamageCondition> CODEC = MapCodecUtil.lazy(InvertedDamageCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createCodec(DamageCondition.CODEC, InvertedDamageCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedDamageCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedDamageCondition.class.getSimpleName(), () -> IInvertedMetaCondition.createStreamCodec(DamageCondition.STREAM_CODEC, InvertedDamageCondition::new));

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return DamageCondition.super.asDisplayString();
	}

}
