package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantCondition(boolean value) implements ConstantMetaCondition {

	public static final Codec<ConstantCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantCondition::new);

	public static final MapCodec<ConstantCondition> MAP_CODEC = ConstantMetaCondition.mapCodec(ConstantCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantCondition> STREAM_CODEC = ConstantMetaCondition.streamCodec(ConstantCondition::new);

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.CONSTANT;
	}

}
