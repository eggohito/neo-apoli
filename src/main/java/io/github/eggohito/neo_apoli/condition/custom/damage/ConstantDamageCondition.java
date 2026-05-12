package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliDamageConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantDamageCondition(boolean value) implements DamageCondition, ConstantMetaCondition {

	public static final Codec<ConstantDamageCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantDamageCondition::new);

	public static final MapCodec<ConstantDamageCondition> MAP_CODEC = ConstantMetaCondition.mapCodec(ConstantDamageCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantDamageCondition> STREAM_CODEC = ConstantMetaCondition.streamCodec(ConstantDamageCondition::new);

	@Override
	public DamageCondition.Type<?> getType() {
		return NeoApoliDamageConditionTypes.CONSTANT;
	}

}
