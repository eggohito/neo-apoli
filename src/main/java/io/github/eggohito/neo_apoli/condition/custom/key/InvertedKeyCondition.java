package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.InvertedMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record InvertedKeyCondition(KeyCondition condition) implements KeyCondition, InvertedMetaCondition<KeyCondition> {

	public static final MapCodec<InvertedKeyCondition> CODEC = MapCodecUtil.lazy(InvertedKeyCondition.class.getSimpleName(), () -> InvertedMetaCondition.createCodec(KeyCondition.CODEC, InvertedKeyCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, InvertedKeyCondition> STREAM_CODEC = StreamCodecUtil.lazy(InvertedKeyCondition.class.getSimpleName(), () -> InvertedMetaCondition.createStreamCodec(KeyCondition.STREAM_CODEC, InvertedKeyCondition::new));

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.INVERTED;
	}

	@Override
	public String asDisplayString() {
		return KeyCondition.super.asDisplayString();
	}

}
