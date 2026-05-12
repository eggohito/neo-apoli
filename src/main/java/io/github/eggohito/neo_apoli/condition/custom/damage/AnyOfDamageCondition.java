package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliDamageConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfDamageCondition(List<DamageCondition> conditions) implements DamageCondition, AnyOfMetaCondition<DamageCondition> {

	public static final MapCodec<AnyOfDamageCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfDamageCondition.class.getSimpleName(), () -> AnyOfMetaCondition.mapCodec(DamageCondition.CODEC, AnyOfDamageCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfDamageCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfDamageCondition.class.getSimpleName(), () -> AnyOfMetaCondition.streamCodec(DamageCondition.STREAM_CODEC, AnyOfDamageCondition::new));

	@Override
	public DamageCondition.Type<?> getType() {
		return NeoApoliDamageConditionTypes.ANY_OF;
	}

}
