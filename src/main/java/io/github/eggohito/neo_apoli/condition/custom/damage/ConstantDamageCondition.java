package io.github.eggohito.neo_apoli.condition.custom.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionType;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantDamageCondition(boolean value) implements DamageCondition, ConstantMetaCondition {

	public static final Codec<ConstantDamageCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantDamageCondition::new);

	public static final MapCodec<ConstantDamageCondition> CODEC = ConstantMetaCondition.createCodec(ConstantDamageCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantDamageCondition> STREAM_CODEC = ConstantMetaCondition.createStreamCodec(ConstantDamageCondition::new);

	@Override
	public DamageConditionType<?> getType() {
		return DamageConditionTypes.CONSTANT;
	}

	@Override
	public String asDisplayString() {
		return DamageCondition.super.asDisplayString();
	}

}
