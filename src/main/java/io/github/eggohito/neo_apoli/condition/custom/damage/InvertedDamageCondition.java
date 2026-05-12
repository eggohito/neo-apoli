package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliDamageConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedDamageCondition(DamageCondition condition) implements DamageCondition, InvertedMetaCondition<DamageCondition> {

	public static final MapCodec<InvertedDamageCondition> MAP_CODEC = MapCodecUtil.lazy(InvertedDamageCondition.class.getSimpleName(), () -> InvertedMetaCondition.mapCodec(DamageCondition.CODEC, InvertedDamageCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedDamageCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedDamageCondition.class.getSimpleName(), () -> InvertedMetaCondition.streamCodec(DamageCondition.STREAM_CODEC, InvertedDamageCondition::new));

	@Override
	public DamageCondition.Type<?> getType() {
		return NeoApoliDamageConditionTypes.INVERTED;
	}

}
