package io.github.eggohito.neo_apoli.condition.custom.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBiEntityConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantBiEntityCondition(boolean value) implements BiEntityCondition, ConstantMetaCondition {

	public static final Codec<ConstantBiEntityCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantBiEntityCondition::new);

	public static final MapCodec<ConstantBiEntityCondition> MAP_CODEC = ConstantMetaCondition.mapCodec(ConstantBiEntityCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantBiEntityCondition> STREAM_CODEC = ConstantMetaCondition.streamCodec(ConstantBiEntityCondition::new);

	@Override
	public BiEntityCondition.Type<?> getType() {
		return NeoApoliBiEntityConditionTypes.CONSTANT;
	}

}
