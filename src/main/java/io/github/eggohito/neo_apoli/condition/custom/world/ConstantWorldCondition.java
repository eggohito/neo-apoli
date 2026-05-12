package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ConstantMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliWorldConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ConstantWorldCondition(boolean value) implements WorldCondition, ConstantMetaCondition {

	public static final Codec<ConstantWorldCondition> INLINE_CODEC = ConstantMetaCondition.createInlineCodec(ConstantWorldCondition::new);

	public static final MapCodec<ConstantWorldCondition> MAP_CODEC = ConstantMetaCondition.mapCodec(ConstantWorldCondition::new);

	public static final StreamCodec<RegistryFriendlyByteBuf, ConstantWorldCondition> STREAM_CODEC = ConstantMetaCondition.streamCodec(ConstantWorldCondition::new);

	@Override
	public WorldCondition.Type<?> getType() {
		return NeoApoliWorldConditionTypes.CONSTANT;
	}

}
