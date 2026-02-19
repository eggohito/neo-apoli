package io.github.eggohito.neo_apoli.condition.custom.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.AnyOfMetaCondition;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionType;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AnyOfKeyCondition(List<KeyCondition> conditions) implements KeyCondition, AnyOfMetaCondition<KeyCondition> {

	public static final MapCodec<AnyOfKeyCondition> MAP_CODEC = MapCodecUtil.lazy(AnyOfKeyCondition.class.getSimpleName(), () -> AnyOfMetaCondition.mapCodec(KeyCondition.CODEC, AnyOfKeyCondition::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, AnyOfKeyCondition> STREAM_CODEC = StreamCodecUtil.lazy(AnyOfKeyCondition.class.getSimpleName(), () -> AnyOfMetaCondition.streamCodec(KeyCondition.STREAM_CODEC, AnyOfKeyCondition::new));

	@Override
	public KeyConditionType<?> getType() {
		return KeyConditionTypes.ANY_OF;
	}

}
